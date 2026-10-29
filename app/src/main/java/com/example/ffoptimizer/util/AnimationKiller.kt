package com.example.ffoptimizer.util

import android.content.Context
import android.provider.Settings
import android.util.Log

object AnimationKiller {

    private const val TAG = "AnimationKiller"

    /**
     * Сбрасывает системные коэффициенты анимации в 0.
     * Требует android.permission.WRITE_SECURE_SETTINGS
     * (выдаётся только через ADB или Shizuku).
     */
    fun zeroAnimations(context: Context) {
        try {
            Settings.Global.putFloat(
                context.contentResolver,
                Settings.Global.WINDOW_ANIMATION_SCALE,
                0.0f
            )
            Settings.Global.putFloat(
                context.contentResolver,
                Settings.Global.TRANSITION_ANIMATION_SCALE,
                0.0f
            )
            Settings.Global.putFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                0.0f
            )
            Log.i(TAG, "All animation scales set to 0.0")
        } catch (e: SecurityException) {
            Log.e(TAG, "WRITE_SECURE_SETTINGS not granted. Use ADB:", e)
            Log.e(TAG, "adb shell pm grant com.example.ffoptimizer android.permission.WRITE_SECURE_SETTINGS")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set animation scales", e)
        }
    }

    fun restoreAnimations(context: Context, scale: Float = 1.0f) {
        try {
            Settings.Global.putFloat(context.contentResolver, Settings.Global.WINDOW_ANIMATION_SCALE, scale)
            Settings.Global.putFloat(context.contentResolver, Settings.Global.TRANSITION_ANIMATION_SCALE, scale)
            Settings.Global.putFloat(context.contentResolver, Settings.Global.ANIMATOR_DURATION_SCALE, scale)
        } catch (_: Exception) {}
    }
}
