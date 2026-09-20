package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val sender: String, // "me" or "friend"
    val timestamp: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false, // Snapchat-style saved in chat
    val isRead: Boolean = false,
    val readTimestamp: Long? = null,
    val ephemeralMode: String = "KEEP", // "KEEP" or "DELETE_AFTER_READ"
    val emojiSticker: String? = null, // e.g. "🤪" if sent as big sticker
    val mediaType: String = "TEXT", // "TEXT", "STICKER", "SNAP"
    val isBurned: Boolean = false // animated vanishing
)
