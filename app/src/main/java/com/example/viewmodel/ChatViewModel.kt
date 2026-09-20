package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ChatRepository
import com.example.data.FriendProfile
import com.example.data.GoogleUserData
import com.example.data.MessageEntity
import com.example.ui.theme.TimeOfDay
import com.example.util.CoupleRealtimeSyncManager
import com.example.util.GoogleAuthManager
import com.example.util.GoogleMessagesManager
import com.example.util.HapticFeedbackManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ChatRepository
    val hapticManager: HapticFeedbackManager = HapticFeedbackManager(application)
    val googleAuthManager: GoogleAuthManager = GoogleAuthManager(application)

    val googleUser: StateFlow<GoogleUserData> = googleAuthManager.currentUser

    private val _isFriendTyping = MutableStateFlow(false)
    val isFriendTyping: StateFlow<Boolean> = _isFriendTyping.asStateFlow()

    private val _currentEphemeralMode = MutableStateFlow("KEEP") // "KEEP" or "DELETE_AFTER_READ"
    val currentEphemeralMode: StateFlow<String> = _currentEphemeralMode.asStateFlow()

    private val _manualTimeOfDay = MutableStateFlow<TimeOfDay?>(null)
    val manualTimeOfDay: StateFlow<TimeOfDay?> = _manualTimeOfDay.asStateFlow()

    private val _lovePingEvent = MutableStateFlow<String?>(null)
    val lovePingEvent: StateFlow<String?> = _lovePingEvent.asStateFlow()

    private val activeBurnJobs = mutableMapOf<Long, Job>()
    private var typingTimeoutJob: Job? = null

    // Real-Time 2-Way Sync Engine connecting to Girlfriend's phone
    val coupleSyncManager = CoupleRealtimeSyncManager(
        onMessageReceived = { clientMsgId, senderRole, senderName, text, mediaType, emojiSticker, ephemeralMode, timestamp ->
            handleIncomingRealtimeMessage(clientMsgId, senderRole, senderName, text, mediaType, emojiSticker, ephemeralMode, timestamp)
        },
        onTypingStateChanged = { isTyping, senderName ->
            _isFriendTyping.value = isTyping
            if (isTyping) {
                hapticManager.vibrateTick()
                typingTimeoutJob?.cancel()
                typingTimeoutJob = viewModelScope.launch {
                    delay(5000)
                    _isFriendTyping.value = false
                }
            }
        },
        onLovePingReceived = { senderName, pingType ->
            viewModelScope.launch {
                val pingEmoji = when (pingType) {
                    "HEART" -> "❤️"
                    "KISS" -> "💋"
                    "HUG" -> "🫂"
                    else -> "💖"
                }
                _lovePingEvent.value = "$senderName sent you a $pingEmoji Love Ping!"
                hapticManager.vibrateMessageReceived()
                delay(3500)
                _lovePingEvent.value = null
            }
        },
        onMessageBurned = { clientMsgId ->
            viewModelScope.launch {
                repository.burnMessageByClientId(clientMsgId)
                hapticManager.vibrateEphemeralBurn()
            }
        },
        onMessageSaveToggled = { clientMsgId, isSaved ->
            viewModelScope.launch {
                repository.setMessageSavedByClientId(clientMsgId, isSaved)
                hapticManager.vibrateTick()
            }
        }
    )

    val isRealtimeConnected: StateFlow<Boolean> = coupleSyncManager.isConnected

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ChatRepository(db.appDao())

        // Ensure girlfriend profile and romantic couple starter messages exist
        viewModelScope.launch {
            repository.friendProfile.collect { profile ->
                if (profile == null) {
                    val initialProfile = FriendProfile(
                        id = 1,
                        name = "My Babe 💖",
                        handle = "my.girlfriend",
                        avatarEmoji = "👸",
                        streakCount = 365,
                        streakEmoji = "❤️",
                        statusMessage = "Forever & always with you 💕",
                        ephemeralDefault = "KEEP",
                        timeOfDayTheme = "AUTO",
                        hapticFeedbackEnabled = true,
                        soundEnabled = true,
                        encryptionKeyFingerprint = "48A2-9E71-F03B-CC89",
                        phoneNumber = "+15551234567",
                        coupleSyncCode = "DAYAN-LOVE-2026",
                        myName = "Dayan",
                        myRole = "BOYFRIEND"
                    )
                    repository.saveFriendProfile(initialProfile)
                } else if (profile.name == "Alex" || profile.coupleSyncCode.isBlank()) {
                    val updated = profile.copy(
                        name = "My Babe 💖",
                        handle = "my.girlfriend",
                        avatarEmoji = "👸",
                        streakCount = if (profile.streakCount == 142) 365 else profile.streakCount,
                        streakEmoji = "❤️",
                        coupleSyncCode = "DAYAN-LOVE-2026",
                        myName = "Dayan",
                        myRole = "BOYFRIEND"
                    )
                    repository.saveFriendProfile(updated)
                    coupleSyncManager.startListening(updated.coupleSyncCode, updated.myRole)
                } else {
                    // Start or refresh real-time sync channel listener
                    coupleSyncManager.startListening(profile.coupleSyncCode, profile.myRole)
                }
            }
        }

        viewModelScope.launch {
            repository.allMessages.collect { list ->
                if (list.isEmpty()) {
                    populateStarterMessages()
                }
            }
        }
    }

    val messages: StateFlow<List<MessageEntity>> = repository.allMessages
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val friendProfile: StateFlow<FriendProfile?> = repository.friendProfile
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private suspend fun populateStarterMessages() {
        val now = System.currentTimeMillis()
        val m1 = MessageEntity(
            text = "Hey Dayan! Happy 365 day anniversary streak ❤️ So happy to text with you here!",
            sender = "friend",
            timestamp = now - 3600000,
            isSaved = true,
            isRead = true,
            ephemeralMode = "KEEP",
            clientMessageId = "init-gf-1"
        )
        val m2 = MessageEntity(
            text = "Hey babe! Loving our private real-time couple chat ✨",
            sender = "me",
            timestamp = now - 1800000,
            isSaved = true,
            isRead = true,
            ephemeralMode = "KEEP",
            clientMessageId = "init-me-1"
        )
        val m3 = MessageEntity(
            text = "Sending you my love 🥰",
            sender = "friend",
            timestamp = now - 600000,
            isSaved = false,
            isRead = true,
            emojiSticker = "🥰",
            mediaType = "STICKER",
            ephemeralMode = "KEEP",
            clientMessageId = "init-gf-2"
        )
        val m4 = MessageEntity(
            text = "🔒 Live 2-way real-time couple sync active. Anything either of us texts appears instantly!",
            sender = "friend",
            timestamp = now - 120000,
            isSaved = false,
            isRead = true,
            ephemeralMode = "KEEP",
            clientMessageId = "init-gf-3"
        )
        repository.insertMessage(m1)
        repository.insertMessage(m2)
        repository.insertMessage(m3)
        repository.insertMessage(m4)
    }

    private fun handleIncomingRealtimeMessage(
        clientMsgId: String,
        senderRole: String,
        senderName: String,
        text: String,
        mediaType: String,
        emojiSticker: String?,
        ephemeralMode: String,
        timestamp: Long
    ) {
        viewModelScope.launch {
            if (repository.hasMessageWithClientId(clientMsgId)) {
                return@launch
            }

            _isFriendTyping.value = false

            val currentMyRole = friendProfile.value?.myRole ?: "BOYFRIEND"
            // If message was sent by the other role, it's incoming ("friend")
            val messageSender = if (senderRole == currentMyRole) "me" else "friend"

            val incomingMessage = MessageEntity(
                text = text,
                sender = messageSender,
                timestamp = timestamp,
                isSaved = false,
                isRead = false,
                ephemeralMode = ephemeralMode,
                emojiSticker = emojiSticker,
                mediaType = mediaType,
                clientMessageId = clientMsgId
            )

            val newId = repository.insertMessage(incomingMessage)
            if (messageSender == "friend") {
                hapticManager.vibrateMessageReceived()
            }

            if (ephemeralMode == "DELETE_AFTER_READ" && messageSender == "friend") {
                startEphemeralCountdown(newId)
            }
        }
    }

    fun dismissLovePing() {
        _lovePingEvent.value = null
    }

    fun setEphemeralMode(mode: String) {
        _currentEphemeralMode.value = mode
        hapticManager.vibrateTick()
        viewModelScope.launch {
            friendProfile.value?.let { current ->
                repository.saveFriendProfile(current.copy(ephemeralDefault = mode))
            }
        }
    }

    fun setTimeOfDayMode(mode: String) {
        when (mode) {
            "AUTO" -> _manualTimeOfDay.value = null
            "DAWN" -> _manualTimeOfDay.value = TimeOfDay.DAWN
            "DAY" -> _manualTimeOfDay.value = TimeOfDay.DAY
            "SUNSET" -> _manualTimeOfDay.value = TimeOfDay.SUNSET
            "NIGHT" -> _manualTimeOfDay.value = TimeOfDay.NIGHT
        }
        hapticManager.vibrateTick()
        viewModelScope.launch {
            friendProfile.value?.let { current ->
                repository.saveFriendProfile(current.copy(timeOfDayTheme = mode))
            }
        }
    }

    fun onUserTyping(isTyping: Boolean) {
        val currentProfile = friendProfile.value
        val myName = currentProfile?.myName ?: (googleUser.value.displayName.ifBlank { "Dayan" })
        val myRole = currentProfile?.myRole ?: "BOYFRIEND"
        coupleSyncManager.broadcastTyping(isTyping, myName, myRole)
    }

    fun sendMessage(text: String, mediaType: String = "TEXT", emojiSticker: String? = null) {
        if (text.isBlank() && emojiSticker == null) return

        val mode = _currentEphemeralMode.value
        val clientMsgId = UUID.randomUUID().toString()
        val currentProfile = friendProfile.value
        val myName = currentProfile?.myName ?: (googleUser.value.displayName.ifBlank { "Dayan" })
        val myRole = currentProfile?.myRole ?: "BOYFRIEND"

        val message = MessageEntity(
            text = text,
            sender = "me",
            timestamp = System.currentTimeMillis(),
            isSaved = false,
            isRead = true,
            ephemeralMode = mode,
            emojiSticker = emojiSticker,
            mediaType = mediaType,
            clientMessageId = clientMsgId
        )

        viewModelScope.launch {
            repository.insertMessage(message)
            hapticManager.vibrateMessageSent()

            // Broadcast instantly to girlfriend's device via real-time couple channel
            coupleSyncManager.broadcastChatMessage(
                clientMsgId = clientMsgId,
                senderRole = myRole,
                senderName = myName,
                text = text,
                mediaType = mediaType,
                emojiSticker = emojiSticker,
                ephemeralMode = mode
            )
        }
    }

    fun sendLovePing(pingType: String = "HEART") {
        val currentProfile = friendProfile.value
        val myName = currentProfile?.myName ?: (googleUser.value.displayName.ifBlank { "Dayan" })
        val myRole = currentProfile?.myRole ?: "BOYFRIEND"
        coupleSyncManager.broadcastLovePing(myName, myRole, pingType)
        hapticManager.vibrateMessageSent()
    }

    fun triggerInstantFriendMessage() {
        // Quick simulator to test girlfriend's incoming snap when testing solo
        viewModelScope.launch {
            _isFriendTyping.value = true
            hapticManager.vibrateTick()
            delay(1500)
            _isFriendTyping.value = false

            val sweetMessages = listOf(
                "Thinking of you my love! ❤️",
                "Can't wait to see you today babe 🥰",
                "You're the sweetest! 💖",
                "Love our cute Snapchat look ✨",
                "Always right here with you 💕"
            )

            val friendMsg = MessageEntity(
                text = sweetMessages.random(),
                sender = "friend",
                timestamp = System.currentTimeMillis(),
                isSaved = false,
                isRead = false,
                ephemeralMode = _currentEphemeralMode.value,
                mediaType = "TEXT",
                clientMessageId = UUID.randomUUID().toString()
            )
            val newId = repository.insertMessage(friendMsg)
            hapticManager.vibrateMessageReceived()

            if (_currentEphemeralMode.value == "DELETE_AFTER_READ") {
                startEphemeralCountdown(newId)
            }
        }
    }

    fun toggleSaveMessage(message: MessageEntity) {
        viewModelScope.launch {
            val newSavedState = !message.isSaved
            repository.setMessageSaved(message.id, newSavedState)
            hapticManager.vibrateTick()

            if (message.clientMessageId.isNotBlank()) {
                coupleSyncManager.broadcastSaveToggle(message.clientMessageId, newSavedState)
            }

            if (newSavedState) {
                activeBurnJobs[message.id]?.cancel()
                activeBurnJobs.remove(message.id)
            } else if (message.ephemeralMode == "DELETE_AFTER_READ") {
                startEphemeralCountdown(message.id)
            }
        }
    }

    fun markMessageRead(message: MessageEntity) {
        if (message.isRead) return
        viewModelScope.launch {
            repository.markMessageRead(message.id)
            if (message.ephemeralMode == "DELETE_AFTER_READ" && !message.isSaved) {
                startEphemeralCountdown(message.id)
            }
        }
    }

    fun startEphemeralCountdown(messageId: Long, delayMillis: Long = 6000L) {
        activeBurnJobs[messageId]?.cancel()
        activeBurnJobs[messageId] = viewModelScope.launch {
            delay(delayMillis)
            val currentList = messages.value
            val msg = currentList.find { it.id == messageId }
            if (msg != null && !msg.isSaved) {
                hapticManager.vibrateEphemeralBurn()
                repository.deleteMessage(messageId)
                if (msg.clientMessageId.isNotBlank()) {
                    coupleSyncManager.broadcastBurnMessage(msg.clientMessageId)
                }
            }
            activeBurnJobs.remove(messageId)
        }
    }

    fun burnMessageImmediately(messageId: Long) {
        viewModelScope.launch {
            val msg = messages.value.find { it.id == messageId }
            hapticManager.vibrateEphemeralBurn()
            repository.deleteMessage(messageId)
            if (msg?.clientMessageId?.isNotBlank() == true) {
                coupleSyncManager.broadcastBurnMessage(msg.clientMessageId)
            }
        }
    }

    fun clearUnsavedMessages() {
        viewModelScope.launch {
            hapticManager.vibrateEphemeralBurn()
            repository.clearUnsavedMessages()
        }
    }

    fun updateFriendProfile(
        name: String,
        handle: String,
        avatar: String,
        streak: Int,
        phoneNumber: String = "+15551234567",
        coupleSyncCode: String? = null
    ) {
        viewModelScope.launch {
            val current = friendProfile.value ?: FriendProfile()
            val newCode = coupleSyncCode ?: current.coupleSyncCode
            val updated = current.copy(
                name = name,
                handle = handle,
                avatarEmoji = avatar,
                streakCount = streak,
                phoneNumber = phoneNumber,
                coupleSyncCode = newCode
            )
            repository.saveFriendProfile(updated)
            coupleSyncManager.startListening(newCode, updated.myRole)
            hapticManager.vibrateTick()
        }
    }

    fun switchUserRole(newRole: String) {
        viewModelScope.launch {
            val current = friendProfile.value ?: FriendProfile()
            val isNowBoyfriend = newRole == "BOYFRIEND"
            val newMyName = if (isNowBoyfriend) "Dayan" else current.name.substringBefore(" ")
            val updated = current.copy(
                myRole = newRole,
                myName = newMyName
            )
            repository.saveFriendProfile(updated)
            coupleSyncManager.startListening(updated.coupleSyncCode, updated.myRole)
            hapticManager.vibrateTick()
        }
    }

    fun openGoogleMessages(prefillText: String = "") {
        val phone = friendProfile.value?.phoneNumber ?: "+15551234567"
        GoogleMessagesManager.openConversation(getApplication(), phone, prefillText)
        hapticManager.vibrateTick()
    }

    fun signInWithDemoGoogle(email: String = "snap.user@gmail.com", displayName: String = "Snap Member") {
        googleAuthManager.signInWithDemoGoogleAccount(email, displayName)
        viewModelScope.launch {
            val current = friendProfile.value ?: FriendProfile()
            repository.saveFriendProfile(current.copy(myName = displayName))
        }
        hapticManager.vibrateMessageReceived()
    }

    fun signOutGoogle() {
        viewModelScope.launch {
            googleAuthManager.signOut()
            hapticManager.vibrateTick()
        }
    }

    override fun onCleared() {
        super.onCleared()
        coupleSyncManager.stopListening()
    }
}
