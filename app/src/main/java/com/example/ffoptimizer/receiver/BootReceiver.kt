package com.example.ffoptimizer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.ffoptimizer.service.GameModeService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.i("BootReceiver", "Boot completed — starting GameModeService")
            val serviceIntent = Intent(context, GameModeService::class.java)
            ContextCompat.startForegroundService(context, serviceIntent)
        }
    }
}
