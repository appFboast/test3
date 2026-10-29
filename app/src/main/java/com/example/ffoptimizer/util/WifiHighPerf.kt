package com.example.ffoptimizer.util

import android.content.Context
import android.net.wifi.WifiManager
import android.util.Log

object WifiHighPerf {

    private const val TAG = "WifiHighPerf"
    private var wifiLock: WifiManager.WifiLock? = null

    /**
     * Берёт Wi-Fi lock в режиме максимальной производительности.
     * Помогает избежать агрессивного power-saving Wi-Fi во время игры.
     */
    fun enable(context: Context) {
        try {
            val wifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager

            if (wifiLock == null) {
                @Suppress("DEPRECATION")
                wifiLock = wifiManager.createWifiLock(
                    WifiManager.WIFI_MODE_FULL_HIGH_PERF,
                    "FFOptimizer::WifiHighPerfLock"
                )
                wifiLock?.setReferenceCounted(false)
            }

            if (wifiLock?.isHeld == false) {
                wifiLock?.acquire()
                Log.i(TAG, "WIFI_MODE_FULL_HIGH_PERF acquired")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to acquire WifiLock", e)
        }
    }

    fun disable() {
        try {
            if (wifiLock?.isHeld == true) {
                wifiLock?.release()
                Log.i(TAG, "WifiLock released")
            }
        } catch (_: Exception) {}
    }
}
