package com.example.ui.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.HapticFeedbackConstants
import android.view.SoundEffectConstants
import android.view.View

class FeedbackHelper(private val context: Context) {
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private val audioManager: AudioManager? =
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager

    private var toneGenerator: ToneGenerator? = try {
        ToneGenerator(AudioManager.STREAM_MUSIC, 80)
    } catch (e: Exception) {
        null
    }

    fun triggerKeyClick(hapticEnabled: Boolean, soundEnabled: Boolean, view: View? = null) {
        if (hapticEnabled) {
            try {
                if (view != null) {
                    view.performHapticFeedback(
                        HapticFeedbackConstants.KEYBOARD_TAP,
                        HapticFeedbackConstants.FLAG_IGNORE_GLOBAL_SETTING
                    )
                } else if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(VibrationEffect.createOneShot(12, VibrationEffect.DEFAULT_AMPLITUDE))
                    } else {
                        @Suppress("DEPRECATION")
                        vibrator.vibrate(12)
                    }
                }
            } catch (e: Exception) {
                // Ignore vibration errors gracefully
            }
        }

        if (soundEnabled) {
            try {
                // Guaranteed sound playback via ToneGenerator on music stream (independent of ringer silent mode)
                if (toneGenerator != null) {
                    toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 25)
                } else {
                    if (view != null) {
                        view.playSoundEffect(SoundEffectConstants.CLICK)
                    } else {
                        audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f)
                    }
                }
            } catch (e: Exception) {
                try {
                    audioManager?.playSoundEffect(AudioManager.FX_KEY_CLICK, 1.0f)
                } catch (e2: Exception) {
                    // Ignore audio errors gracefully
                }
            }
        }
    }
}
