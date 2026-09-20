package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object GoogleMessagesManager {

    private const val GOOGLE_MESSAGES_PACKAGE = "com.google.android.apps.messaging"

    /**
     * Opens a 1-on-1 SMS/RCS conversation with the chosen friend in Google Messages or default SMS app.
     * Pre-fills the recipient and optionally pre-fills message text.
     */
    fun openConversation(context: Context, phoneNumber: String, prefillText: String = "") {
        try {
            val cleanPhone = phoneNumber.filter { it.isDigit() || it == '+' }
            val uri = Uri.parse("smsto:$cleanPhone")
            val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
                if (prefillText.isNotBlank()) {
                    putExtra("sms_body", prefillText)
                }
                setPackage(GOOGLE_MESSAGES_PACKAGE)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            // Check if Google Messages is installed
            val packageManager = context.packageManager
            val canResolveGoogleMessages = intent.resolveActivity(packageManager) != null

            if (canResolveGoogleMessages) {
                context.startActivity(intent)
            } else {
                // Fallback to standard SMS intent (e.g. system default SMS app or any installed messaging app)
                val genericIntent = Intent(Intent.ACTION_SENDTO, uri).apply {
                    if (prefillText.isNotBlank()) {
                        putExtra("sms_body", prefillText)
                    }
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(genericIntent)
            }
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No messaging application available", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open messaging: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Quickly sends a message directly to Google Messages with prefilled text
     */
    fun textOnePerson(context: Context, phoneNumber: String, messageText: String) {
        openConversation(context, phoneNumber, messageText)
    }
}
