package com.example.ffoptimizer.util

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log

object ProcessKiller {

    private const val TAG = "ProcessKiller"

    // Критически важные пакеты, которые нельзя трогать
    private val PROTECTED_PACKAGES = setOf(
        "android",
        "com.android.systemui",
        "com.android.phone",
        "com.android.server.telecom",
        "com.google.android.gms",
        "com.google.android.gsf",
        "com.android.vending",
        "com.dts.freefireth",          // сама игра
        "com.dts.freefiremax",         // если есть max версия
        "com.example.ffoptimizer"      // наше приложение
    )

    /**
     * Агрессивно убивает фоновые процессы сторонних приложений.
     * Без root/Shizuku это максимум того, что разрешает система.
     */
    fun killAllBackground(context: Context) {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val pm = context.packageManager

        try {
            val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            var killed = 0

            for (app in apps) {
                val pkg = app.packageName
                // Только сторонние приложения
                if (app.flags and ApplicationInfo.FLAG_SYSTEM == 0 &&
                    pkg !in PROTECTED_PACKAGES
                ) {
                    try {
                        am.killBackgroundProcesses(pkg)
                        killed++
                    } catch (e: Exception) {
                        // ignore
                    }
                }
            }
            Log.i(TAG, "Killed background processes of ~$killed apps")
        } catch (e: Exception) {
            Log.e(TAG, "Error while killing processes", e)
        }
    }
}
