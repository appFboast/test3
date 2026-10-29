package com.example.ffoptimizer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.ffoptimizer.MainActivity
import com.example.ffoptimizer.R
import java.net.InetAddress
import kotlin.concurrent.thread

class OverlayService : Service() {

    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false

    private var lastPing = -1L
    private var batteryTemp = 0f

    override fun onCreate() {
        super.onCreate()
        startForeground(NOTIFICATION_ID, createNotification())
        showOverlay()
        startUpdating()
    }

    private fun showOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_hud, null)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 30
            y = 200
        }

        // Перетаскивание
        overlayView?.setOnTouchListener(object : View.OnTouchListener {
            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        initialX = params.x
                        initialY = params.y
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()
                        windowManager?.updateViewLayout(overlayView, params)
                        return true
                    }
                }
                return false
            }
        })

        try {
            windowManager?.addView(overlayView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startUpdating() {
        isRunning = true
        handler.post(object : Runnable {
            override fun run() {
                if (!isRunning) return
                updateHud()
                measurePingAsync()
                handler.postDelayed(this, 2000)
            }
        })
    }

    private fun updateHud() {
        val pingView = overlayView?.findViewById<TextView>(R.id.tvPing)
        val tempView = overlayView?.findViewById<TextView>(R.id.tvTemp)
        val statusView = overlayView?.findViewById<TextView>(R.id.tvStatus)

        val bm = getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        batteryTemp = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW).toFloat() // fallback
        // Более правильный способ температуры:
        try {
            val intentFilter = android.content.IntentFilter(Intent.ACTION_BATTERY_CHANGED)
            val batteryStatus = registerReceiver(null, intentFilter)
            batteryTemp = (batteryStatus?.getIntExtra("temperature", 0) ?: 0) / 10f
        } catch (_: Exception) {}

        pingView?.text = if (lastPing >= 0) "Ping: ${lastPing}ms" else "Ping: --"
        tempView?.text = "Temp: ${"%.1f".format(batteryTemp)}°C"
        statusView?.text = "OPTIMIZER: ON"
    }

    private fun measurePingAsync() {
        thread {
            try {
                val start = System.currentTimeMillis()
                val reachable = InetAddress.getByName("1.1.1.1").isReachable(1500)
                val ping = System.currentTimeMillis() - start
                lastPing = if (reachable) ping else -1
            } catch (_: Exception) {
                lastPing = -1
            }
        }
    }

    private fun createNotification(): Notification {
        val channelId = "ff_overlay_channel"
        val nm = getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(channelId, "HUD Overlay", NotificationManager.IMPORTANCE_LOW)
        )

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("FF HUD Overlay")
            .setContentText("Плавающая панель активна")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentIntent(
                PendingIntent.getActivity(
                    this, 0,
                    Intent(this, MainActivity::class.java),
                    PendingIntent.FLAG_IMMUTABLE
                )
            )
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    override fun onDestroy() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
        try {
            if (overlayView != null) {
                windowManager?.removeView(overlayView)
            }
        } catch (_: Exception) {}
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        private const val NOTIFICATION_ID = 1002
    }
}
