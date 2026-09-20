package com.example.util

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * Real-time 2-way couple sync manager connecting you and your girlfriend across devices.
 * Uses a dedicated, private pub/sub channel for sub-second live texting, real-time typing indicators,
 * love pings, and synchronized snaps.
 */
class CoupleRealtimeSyncManager(
    private val onMessageReceived: (
        clientMsgId: String,
        senderRole: String,
        senderName: String,
        text: String,
        mediaType: String,
        emojiSticker: String?,
        ephemeralMode: String,
        timestamp: Long
    ) -> Unit,
    private val onTypingStateChanged: (isTyping: Boolean, senderName: String) -> Unit,
    private val onLovePingReceived: (senderName: String, pingType: String) -> Unit,
    private val onMessageBurned: (clientMsgId: String) -> Unit,
    private val onMessageSaveToggled: (clientMsgId: String, isSaved: Boolean) -> Unit
) {
    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS) // Indefinite stream timeout for real-time SSE
        .connectTimeout(15, TimeUnit.SECONDS)
        .build()

    private val fastPostClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .build()

    private var listenerJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _isConnected = MutableStateFlow(false)
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private var currentTopic: String = "snap-couple-dayan-love"
    private var mySenderId: String = UUID.randomUUID().toString()

    fun startListening(coupleCode: String, myRole: String) {
        val cleanCode = coupleCode.trim().lowercase().replace(Regex("[^a-z0-9]"), "-")
        val topic = "snap-couple-$cleanCode"
        if (topic == currentTopic && listenerJob?.isActive == true) return

        currentTopic = topic
        listenerJob?.cancel()

        listenerJob = scope.launch {
            while (isActive) {
                try {
                    _isConnected.value = true
                    val request = Request.Builder()
                        .url("https://ntfy.sh/$currentTopic/json")
                        .header("Accept", "text/event-stream")
                        .build()

                    val response = client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        _isConnected.value = false
                        delay(4000)
                        continue
                    }

                    val inputStream = response.body?.byteStream()
                    if (inputStream == null) {
                        _isConnected.value = false
                        delay(3000)
                        continue
                    }

                    val reader = BufferedReader(InputStreamReader(inputStream))
                    var line: String? = null
                    while (isActive && reader.readLine().also { line = it } != null) {
                        val currentLine = line?.trim() ?: continue
                        if (currentLine.isEmpty()) continue

                        try {
                            val outerJson = JSONObject(currentLine)
                            if (outerJson.optString("event") == "message") {
                                val messageStr = outerJson.optString("message")
                                if (messageStr.isNotBlank()) {
                                    handleIncomingEvent(messageStr, myRole)
                                }
                            }
                        } catch (e: Exception) {
                            Log.w("CoupleSync", "Failed to parse json line: $currentLine", e)
                        }
                    }
                } catch (e: Exception) {
                    _isConnected.value = false
                    Log.d("CoupleSync", "Stream disconnected, retrying: ${e.message}")
                    delay(3000)
                }
            }
        }
    }

    fun stopListening() {
        listenerJob?.cancel()
        listenerJob = null
        _isConnected.value = false
    }

    private fun handleIncomingEvent(rawPayload: String, myRole: String) {
        try {
            val json = JSONObject(rawPayload)
            val senderId = json.optString("senderId")
            if (senderId == mySenderId) {
                // Ignore our own echo
                return
            }

            val type = json.optString("type")
            val senderRole = json.optString("senderRole")
            val senderName = json.optString("senderName", "My Babe")

            when (type) {
                "CHAT_MESSAGE" -> {
                    val clientMsgId = json.optString("clientMsgId")
                    val text = json.optString("text")
                    val mediaType = json.optString("mediaType", "TEXT")
                    val emojiSticker = json.optString("emojiSticker").takeIf { it.isNotBlank() }
                    val ephemeralMode = json.optString("ephemeralMode", "KEEP")
                    val timestamp = json.optLong("timestamp", System.currentTimeMillis())

                    onMessageReceived(
                        clientMsgId,
                        senderRole,
                        senderName,
                        text,
                        mediaType,
                        emojiSticker,
                        ephemeralMode,
                        timestamp
                    )
                }
                "TYPING" -> {
                    val isTyping = json.optBoolean("isTyping", false)
                    onTypingStateChanged(isTyping, senderName)
                }
                "LOVE_PING" -> {
                    val pingType = json.optString("pingType", "HEART")
                    onLovePingReceived(senderName, pingType)
                }
                "BURN_MESSAGE" -> {
                    val clientMsgId = json.optString("clientMsgId")
                    if (clientMsgId.isNotBlank()) {
                        onMessageBurned(clientMsgId)
                    }
                }
                "SAVE_TOGGLE" -> {
                    val clientMsgId = json.optString("clientMsgId")
                    val isSaved = json.optBoolean("isSaved", false)
                    if (clientMsgId.isNotBlank()) {
                        onMessageSaveToggled(clientMsgId, isSaved)
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("CoupleSync", "Failed to parse incoming payload", e)
        }
    }

    fun broadcastChatMessage(
        clientMsgId: String,
        senderRole: String,
        senderName: String,
        text: String,
        mediaType: String = "TEXT",
        emojiSticker: String? = null,
        ephemeralMode: String = "KEEP"
    ) {
        scope.launch {
            val json = JSONObject().apply {
                put("senderId", mySenderId)
                put("type", "CHAT_MESSAGE")
                put("clientMsgId", clientMsgId)
                put("senderRole", senderRole)
                put("senderName", senderName)
                put("text", text)
                put("mediaType", mediaType)
                put("emojiSticker", emojiSticker ?: "")
                put("ephemeralMode", ephemeralMode)
                put("timestamp", System.currentTimeMillis())
            }
            postPayload(json.toString())
        }
    }

    fun broadcastTyping(isTyping: Boolean, senderName: String, senderRole: String) {
        scope.launch {
            val json = JSONObject().apply {
                put("senderId", mySenderId)
                put("type", "TYPING")
                put("senderRole", senderRole)
                put("senderName", senderName)
                put("isTyping", isTyping)
            }
            postPayload(json.toString())
        }
    }

    fun broadcastLovePing(senderName: String, senderRole: String, pingType: String = "HEART") {
        scope.launch {
            val json = JSONObject().apply {
                put("senderId", mySenderId)
                put("type", "LOVE_PING")
                put("senderRole", senderRole)
                put("senderName", senderName)
                put("pingType", pingType)
                put("timestamp", System.currentTimeMillis())
            }
            postPayload(json.toString())
        }
    }

    fun broadcastBurnMessage(clientMsgId: String) {
        scope.launch {
            val json = JSONObject().apply {
                put("senderId", mySenderId)
                put("type", "BURN_MESSAGE")
                put("clientMsgId", clientMsgId)
            }
            postPayload(json.toString())
        }
    }

    fun broadcastSaveToggle(clientMsgId: String, isSaved: Boolean) {
        scope.launch {
            val json = JSONObject().apply {
                put("senderId", mySenderId)
                put("type", "SAVE_TOGGLE")
                put("clientMsgId", clientMsgId)
                put("isSaved", isSaved)
            }
            postPayload(json.toString())
        }
    }

    private fun postPayload(payload: String) {
        try {
            val body = payload.toRequestBody("application/json; charset=utf-8".toMediaType())
            val request = Request.Builder()
                .url("https://ntfy.sh/$currentTopic")
                .post(body)
                .build()

            val response = fastPostClient.newCall(request).execute()
            response.close()
        } catch (e: Exception) {
            Log.w("CoupleSync", "Failed to broadcast: ${e.message}")
        }
    }
}
