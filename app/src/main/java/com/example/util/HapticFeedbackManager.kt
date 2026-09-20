package com.example.util

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager

class HapticFeedbackManager(private val context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    /**
     * Distinctive double-pulse haptic feedback for every message received.
     */
    fun vibrateMessageReceived() {
        vibrator ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crisp double-pulse: wait 0ms, buzz 45ms, pause 70ms, buzz 60ms
            val timings = longArrayOf(0, 45, 70, 60)
            val amplitudes = intArrayOf(0, 180, 0, 255)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 45, 70, 60), -1)
        }
    }

    /**
     * Subtle light haptic when user sends a message.
     */
    fun vibrateMessageSent() {
        vibrator ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(25, 140))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(25)
        }
    }

    /**
     * Ephemeral burn sensation when an ephemeral message dissolves / deletes.
     */
    fun vibrateEphemeralBurn() {
        vibrator ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val timings = longArrayOf(0, 30, 40, 25, 40, 20)
            val amplitudes = intArrayOf(0, 100, 0, 150, 0, 80)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(longArrayOf(0, 30, 40, 25, 40, 20), -1)
        }
    }

    /**
     * Crisp tap feedback for emoji reactions and saving messages.
     */
    fun vibrateTick() {
        vibrator ?: return
        if (!vibrator.hasVibrator()) return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_TICK))
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(18, 120))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(18)
        }
    }
}
