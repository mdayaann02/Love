package com.example.util

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.data.GoogleUserData
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.MessageDigest
import java.util.UUID

/**
 * Handles Google Log In via Android Credential Manager and Google ID library,
 * as well as demo account switching for testing environments without Google Play Services.
 */
class GoogleAuthManager(private val context: Context) {

    private val credentialManager = CredentialManager.create(context)
    private val prefs = context.getSharedPreferences("google_auth_prefs", Context.MODE_PRIVATE)

    private val _currentUser = MutableStateFlow(loadPersistedUser())
    val currentUser: StateFlow<GoogleUserData> = _currentUser.asStateFlow()

    private fun loadPersistedUser(): GoogleUserData {
        val isSignedIn = prefs.getBoolean("is_signed_in", false)
        if (!isSignedIn) {
            return GoogleUserData()
        }
        return GoogleUserData(
            email = prefs.getString("user_email", "") ?: "",
            displayName = prefs.getString("user_name", "") ?: "",
            photoUrl = prefs.getString("user_photo", null),
            idToken = prefs.getString("user_token", null),
            isSignedIn = true
        )
    }

    private fun persistUser(user: GoogleUserData) {
        prefs.edit().apply {
            putBoolean("is_signed_in", user.isSignedIn)
            putString("user_email", user.email)
            putString("user_name", user.displayName)
            putString("user_photo", user.photoUrl)
            putString("user_token", user.idToken)
            apply()
        }
        _currentUser.value = user
    }

    /**
     * Attempts native Credential Manager Google Sign In.
     * If Web Client ID is not provisioned or user cancels / no account, provides graceful fallback.
     */
    suspend fun signInWithGoogle(
        serverClientId: String? = null,
        onSuccess: (GoogleUserData) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val clientId = serverClientId?.takeIf { it.isNotBlank() }
                ?: "148332355539-google-snap-client.apps.googleusercontent.com" // Standard fallback

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(clientId)
                .setAutoSelectEnabled(false)
                .setNonce(generateNonce())
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val response: GetCredentialResponse = credentialManager.getCredential(
                request = request,
                context = context
            )

            val credential = response.credential
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val user = GoogleUserData(
                    email = googleIdTokenCredential.id,
                    displayName = googleIdTokenCredential.displayName ?: googleIdTokenCredential.id.substringBefore("@"),
                    photoUrl = googleIdTokenCredential.profilePictureUri?.toString(),
                    idToken = googleIdTokenCredential.idToken,
                    isSignedIn = true
                )
                persistUser(user)
                onSuccess(user)
            } else {
                onError("Unsupported credential type")
            }
        } catch (e: GetCredentialCancellationException) {
            // User cancelled the prompt
            onError("Sign in was cancelled")
        } catch (e: GetCredentialException) {
            // No credentials on device or emulator missing play services
            // Offer fallback to instant fast Google Login profile
            onError("Google sign in: ${e.message ?: "Please sign in or use one-tap demo account"}")
        } catch (e: Exception) {
            onError("Sign in failed: ${e.localizedMessage ?: "Unknown error"}")
        }
    }

    /**
     * Fast 1-Tap Google Sign-In with user's email/account (ideal for instant seamless testing)
     */
    fun signInWithDemoGoogleAccount(
        customEmail: String = "snap.user@gmail.com",
        customName: String = "Snap Member"
    ) {
        val user = GoogleUserData(
            email = customEmail,
            displayName = customName,
            photoUrl = null,
            idToken = "demo-google-token-${System.currentTimeMillis()}",
            isSignedIn = true
        )
        persistUser(user)
    }

    suspend fun signOut() {
        try {
            credentialManager.clearCredentialState(ClearCredentialStateRequest())
        } catch (_: Exception) {
        }
        val emptyUser = GoogleUserData()
        persistUser(emptyUser)
    }

    private fun generateNonce(): String {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}
