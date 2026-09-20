package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_profile")
data class FriendProfile(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "My Babe 💖",
    val handle: String = "my.girlfriend",
    val avatarEmoji: String = "👸",
    val streakCount: Int = 365,
    val streakEmoji: String = "❤️",
    val statusMessage: String = "Forever & always with you 💕",
    val ephemeralDefault: String = "KEEP", // "KEEP" or "DELETE_AFTER_READ"
    val timeOfDayTheme: String = "AUTO", // "AUTO", "DAWN", "DAY", "SUNSET", "NIGHT"
    val hapticFeedbackEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val encryptionKeyFingerprint: String = "48A2-9E71-F03B-CC89",
    val phoneNumber: String = "+15551234567", // Connected phone number for Google Messages 1-on-1 SMS integration
    val coupleSyncCode: String = "DAYAN-LOVE-2026", // Real-time shared couple channel
    val myName: String = "Dayan",
    val myRole: String = "BOYFRIEND", // "BOYFRIEND" or "GIRLFRIEND"
    val girlfriendEmail: String = "",
    val isRealtimeConnected: Boolean = true
)
