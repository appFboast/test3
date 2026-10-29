package com.example.ffoptimizer.service

import android.content.Intent
import android.net.VpnService
import android.os.ParcelFileDescriptor
import android.util.Log

/**
 * Упрощённый локальный VPN только для принудительного DNS (Cloudflare + Google).
 * Не делает полноценный туннель — только DNS + базовая маршрутизация.
 * Для серьёзного стабилизатора нужен tun2socks + пакетная обработка.
 */
class VpnDnsService : VpnService() {

    private var vpnInterface: ParcelFileDescriptor? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (vpnInterface == null) {
            try {
                val builder = Builder()
                    .setSession("FF DNS Booster")
                    .addAddress("10.0.0.2", 32)
                    .addDnsServer("1.1.1.1")      // Cloudflare
                    .addDnsServer("1.0.0.1")
                    .addDnsServer("8.8.8.8")      // Google
                    .addDnsServer("8.8.4.4")
                    .addRoute("0.0.0.0", 0)
                    .setMtu(1500)
                    .setBlocking(false)

                vpnInterface = builder.establish()
                Log.i(TAG, "VPN interface established")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to establish VPN", e)
                stopSelf()
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        try {
            vpnInterface?.close()
        } catch (_: Exception) {}
        vpnInterface = null
        Log.i(TAG, "VPN destroyed")
        super.onDestroy()
    }

    override fun onRevoke() {
        onDestroy()
        super.onRevoke()
    }

    companion object {
        private const val TAG = "VpnDnsService"
    }
}
