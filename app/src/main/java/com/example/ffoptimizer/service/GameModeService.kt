package com.example.ffoptimizer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.usage.UsageStatsManager
import android.content.Context
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.ffoptimizer.MainActivity
import com.example.ffoptimizer.R
import com.example.ffoptimizer.util.ProcessKiller
import com.example.ffoptimizer.util.WifiHighPerf
import kotlinx.coroutines.*

class GameModeService : Service() {

    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var wakeLock: PowerManager.WakeLock? = null
    private val TARGET_PACKAGE = "com.dts.freefireth"
    private var isGameActive = false

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification("Ожидание запуска Free Fire..."))
        acquireWakeLock()
        startMonitoring()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun startMonitoring() {
        scope.launch {
            while (isActive) {
                val running = isFreeFireRunning()
                if (running && !isGameActive) {
                    activateMaxPerformance()
                    isGameActive = true
                } else if (!running && isGameActive) {
                    isGameActive = false
                    updateNotification("Ожидание запуска Free Fire...")
                }
                delay(1500)
            }
        }
    }

    private fun isFreeFireRunning(): Boolean {
        return try {
            val usm = getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
            val end = System.currentTimeMillis()
            val start = end - 7000
            val stats = usm.queryUsageStats(UsageStatsManager.INTERVAL_BEST, start, end)
            stats?.any {
                it.packageName == TARGET_PACKAGE && it.lastTimeUsed >= start
            } == true
        } catch (e: Exception) {
            false
        }
    }

    private fun activateMaxPerformance() {
        // Агрессивная очистка фоновых процессов
        ProcessKiller.killAllBackground(this)

        // Удержание CPU
        wakeLock?.let {
            if (!it.isHeld) {
                it.acquire(15 * 60 * 1000L) // 15 минут
            }
        }

        // Wi-Fi high performance
        WifiHighPerf.enable(this)

        updateNotification("РЕЖИМ МАКС. ПРОИЗВОДИТЕЛЬНОСТИ АКТИВЕН")
    }

    private fun acquireWakeLock() {
        val pm = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock = pm.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "FFOptimizer::GameWakeLock"
        ).apply {
            setReferenceCounted(false)
        }
    }

    private fun createNotification(text: String): Notification {
        val channelId = "ff_game_mode_channel"
        val nm = getSystemService(NotificationManager::class.java)
        val channel = NotificationChannel(
            channelId,
            "Game Mode Service",
            NotificationManager.IMPORTANCE_LOW
        )
        nm.createNotificationChannel(channel)

        val pendingIntent = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("FF Extreme Optimizer")
            .setContentText(text)
            .setSmallIcon(android.R.drawable.ic_menu_manage)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun updateNotification(text: String) {
        val nm = getSystemService(NotificationManager::class.java)
        nm.notify(NOTIFICATION_ID, createNotification(text))
    }

    override fun onDestroy() {
        scope.cancel()
        wakeLock?.let {
            if (it.isHeld) it.release()
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 1001
    }
}
