package com.example.ffoptimizer.util

import android.content.Context
import android.provider.Settings
import android.util.Log

object DpiHelper {

    private const val TAG = "DpiHelper"

    /**
     * Попытка изменить density через Settings.
     * На большинстве устройств требует WRITE_SECURE_SETTINGS + перезапуск SystemUI
     * или использование `wm density` через ADB/Shizuku.
     */
    fun setDensity(context: Context, dpi: Int) {
        try {
            Settings.Secure.putInt(
                context.contentResolver,
                "display_density_forced",
                dpi
            )
            Log.i(TAG, "display_density_forced set to $dpi")
        } catch (e: SecurityException) {
            Log.e(TAG, "No WRITE_SECURE_SETTINGS. Use ADB: adb shell wm density $dpi")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to set density", e)
        }
    }

    fun resetDensity(context: Context) {
        try {
            Settings.Secure.putString(
                context.contentResolver,
                "display_density_forced",
                null
            )
            Log.i(TAG, "display_density_forced reset")
        } catch (_: Exception) {}
    }
}
