package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ChatRepository
import com.example.data.FriendProfile
import com.example.data.MessageEntity
import com.example.ui.theme.TimeOfDay
import com.example.util.HapticFeedbackManager
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: ChatRepository
    val hapticManager: HapticFeedbackManager = HapticFeedbackManager(application)

    init {
        val db = AppDatabase.getDatabase(application)
        repository = ChatRepository(db.appDao())

        // Ensure initial profile and starter messages exist
        viewModelScope.launch {
            repository.friendProfile.collect { profile ->
                if (profile == null) {
                    val initialProfile = FriendProfile(
                        id = 1,
                        name = "Alex",
                        handle = "alex.snap",
                        avatarEmoji = "👻",
                        streakCount = 142,
                        streakEmoji = "🔥",
                        statusMessage = "Live fast, snap faster 💛",
                        ephemeralDefault = "KEEP",
                        timeOfDayTheme = "AUTO",
                        hapticFeedbackEnabled = true,
                        soundEnabled = true,
                        encryptionKeyFingerprint = "48A2-9E71-F03B-CC89"
                    )
                    repository.saveFriendProfile(initialProfile)
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

    private val _isFriendTyping = MutableStateFlow(false)
    val isFriendTyping: StateFlow<Boolean> = _isFriendTyping.asStateFlow()

    private val _currentEphemeralMode = MutableStateFlow("KEEP") // "KEEP" or "DELETE_AFTER_READ"
    val currentEphemeralMode: StateFlow<String> = _currentEphemeralMode.asStateFlow()

    private val _manualTimeOfDay = MutableStateFlow<TimeOfDay?>(null)
    val manualTimeOfDay: StateFlow<TimeOfDay?> = _manualTimeOfDay.asStateFlow()

    private val activeBurnJobs = mutableMapOf<Long, Job>()

    private suspend fun populateStarterMessages() {
        val now = System.currentTimeMillis()
        val m1 = MessageEntity(
            text = "Yo! 142 day streak today 🔥 don't forget to send a snap!",
            sender = "friend",
            timestamp = now - 3600000,
            isSaved = true,
            isRead = true,
            ephemeralMode = "KEEP"
        )
        val m2 = MessageEntity(
            text = "Haha got you covered! Working on the new liquid glass look ✨",
            sender = "me",
            timestamp = now - 1800000,
            isSaved = true,
            isRead = true,
            ephemeralMode = "KEEP"
        )
        val m3 = MessageEntity(
            text = "Check out this funny derp face 🤪",
            sender = "friend",
            timestamp = now - 600000,
            isSaved = false,
            isRead = true,
            emojiSticker = "🤪",
            mediaType = "STICKER",
            ephemeralMode = "KEEP"
        )
        val m4 = MessageEntity(
            text = "🔒 Encrypted single-person chat active. Toggle 'Delete After Read' for instant disappearing snaps!",
            sender = "friend",
            timestamp = now - 120000,
            isSaved = false,
            isRead = true,
            ephemeralMode = "KEEP"
        )
        repository.insertMessage(m1)
        repository.insertMessage(m2)
        repository.insertMessage(m3)
        repository.insertMessage(m4)
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

    fun sendMessage(text: String, mediaType: String = "TEXT", emojiSticker: String? = null) {
        if (text.isBlank() && emojiSticker == null) return

        val mode = _currentEphemeralMode.value
        val message = MessageEntity(
            text = text,
            sender = "me",
            timestamp = System.currentTimeMillis(),
            isSaved = false,
            isRead = true,
            ephemeralMode = mode,
            emojiSticker = emojiSticker,
            mediaType = mediaType
        )

        viewModelScope.launch {
            repository.insertMessage(message)
            hapticManager.vibrateMessageSent()

            // Trigger simulated friend reply after a realistic typing interval
            triggerFriendReplySequence(text, emojiSticker)
        }
    }

    private fun triggerFriendReplySequence(userText: String, sticker: String?) {
        viewModelScope.launch {
            delay(700)
            _isFriendTyping.value = true
            hapticManager.vibrateTick()
            delay(2200)
            _isFriendTyping.value = false

            val replies = listOf(
                "Bruh no way 💀",
                "OMG that's hilarious 😭",
                "🔥 Streak saved for today!",
                "Look at this one 🗿",
                "Wait till you see my snap 👀",
                "Pure liquid glass aesthetic ✨",
                "Did that just disappear? 🤫",
                "100% encrypted vibes only 🔒",
                "Send another meme 🌮",
                "Hahaha you're too funny 🤪"
            )
            val funnyStickers = listOf("🤪", "🔥", "💀", "🗿", "💅", "🐒", "🌮", "🚀")

            val replyText: String
            val replySticker: String?
            val replyType: String

            if (sticker != null) {
                replyText = ""
                replySticker = funnyStickers.random()
                replyType = "STICKER"
            } else {
                replyText = replies.random()
                replySticker = null
                replyType = "TEXT"
            }

            val friendMsg = MessageEntity(
                text = replyText,
                sender = "friend",
                timestamp = System.currentTimeMillis(),
                isSaved = false,
                isRead = false,
                ephemeralMode = _currentEphemeralMode.value,
                emojiSticker = replySticker,
                mediaType = replyType
            )

            val newId = repository.insertMessage(friendMsg)
            // MANDATORY: Haptic feedback for every message received!
            hapticManager.vibrateMessageReceived()

            // If ephemeral delete after read is active, handle auto-read countdown
            if (_currentEphemeralMode.value == "DELETE_AFTER_READ") {
                startEphemeralCountdown(newId)
            }
        }
    }

    fun triggerInstantFriendMessage() {
        viewModelScope.launch {
            _isFriendTyping.value = true
            hapticManager.vibrateTick()
            delay(1500)
            _isFriendTyping.value = false

            val friendMsg = MessageEntity(
                text = "⚡ Instant snap test! Feeling the haptic pulse? 💛",
                sender = "friend",
                timestamp = System.currentTimeMillis(),
                isSaved = false,
                isRead = false,
                ephemeralMode = _currentEphemeralMode.value,
                mediaType = "TEXT"
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

            // If it was scheduled for ephemeral burn and now saved, cancel the burn
            if (newSavedState) {
                activeBurnJobs[message.id]?.cancel()
                activeBurnJobs.remove(message.id)
            } else if (message.ephemeralMode == "DELETE_AFTER_READ") {
                // If un-saved and in delete after read mode, trigger burn countdown
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
            // Double check if message is saved before deleting
            val currentList = messages.value
            val msg = currentList.find { it.id == messageId }
            if (msg != null && !msg.isSaved) {
                hapticManager.vibrateEphemeralBurn()
                repository.deleteMessage(messageId)
            }
            activeBurnJobs.remove(messageId)
        }
    }

    fun burnMessageImmediately(messageId: Long) {
        viewModelScope.launch {
            hapticManager.vibrateEphemeralBurn()
            repository.deleteMessage(messageId)
        }
    }

    fun clearUnsavedMessages() {
        viewModelScope.launch {
            hapticManager.vibrateEphemeralBurn()
            repository.clearUnsavedMessages()
        }
    }

    fun updateFriendProfile(name: String, handle: String, avatar: String, streak: Int) {
        viewModelScope.launch {
            val current = friendProfile.value ?: FriendProfile()
            repository.saveFriendProfile(
                current.copy(
                    name = name,
                    handle = handle,
                    avatarEmoji = avatar,
                    streakCount = streak
                )
            )
            hapticManager.vibrateTick()
        }
    }
}
