package com.example.data

data class GoogleUserData(
    val email: String = "",
    val displayName: String = "",
    val photoUrl: String? = null,
    val idToken: String? = null,
    val isSignedIn: Boolean = false
)
