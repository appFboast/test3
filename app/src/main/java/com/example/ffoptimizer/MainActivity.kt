package com.example.ffoptimizer

import android.Manifest
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.ffoptimizer.databinding.ActivityMainBinding
import com.example.ffoptimizer.service.GameModeService
import com.example.ffoptimizer.service.OverlayService
import com.example.ffoptimizer.service.VpnDnsService
import com.example.ffoptimizer.ui.MainPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        checkAndRequestPermissions()
    }

    private fun setupViewPager() {
        val adapter = MainPagerAdapter(this)
        binding.viewPager.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Главная"
                1 -> "iPhone Mode"
                2 -> "Сеть"
                else -> ""
            }
        }.attach()
    }

    private fun checkAndRequestPermissions() {
        // Usage Access
        if (!hasUsageStatsPermission()) {
            Toast.makeText(this, "Нужен доступ к Usage Access", Toast.LENGTH_LONG).show()
            startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS))
        }

        // Overlay
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "Нужно разрешение Overlay", Toast.LENGTH_LONG).show()
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

        // Battery optimization
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            try {
                startActivity(intent)
            } catch (_: Exception) {}
        }
    }

    private fun hasUsageStatsPermission(): Boolean {
        val appOps = getSystemService(Context.APP_OPS_SERVICE) as AppOpsManager
        val mode = appOps.checkOpNoThrow(
            AppOpsManager.OPSTR_GET_USAGE_STATS,
            android.os.Process.myUid(),
            packageName
        )
        return mode == AppOpsManager.MODE_ALLOWED
    }

    fun startGameModeService() {
        val intent = Intent(this, GameModeService::class.java)
        ContextCompat.startForegroundService(this, intent)
        Toast.makeText(this, "Game Mode Service запущен", Toast.LENGTH_SHORT).show()
    }

    fun stopGameModeService() {
        stopService(Intent(this, GameModeService::class.java))
        Toast.makeText(this, "Game Mode Service остановлен", Toast.LENGTH_SHORT).show()
    }

    fun startVpn() {
        val intent = VpnDnsService.prepare(this)
        if (intent != null) {
            startActivityForResult(intent, 1001)
        } else {
            startService(Intent(this, VpnDnsService::class.java))
            Toast.makeText(this, "VPN DNS Booster запущен", Toast.LENGTH_SHORT).show()
        }
    }

    fun stopVpn() {
        stopService(Intent(this, VpnDnsService::class.java))
        Toast.makeText(this, "VPN остановлен", Toast.LENGTH_SHORT).show()
    }

    fun startOverlay() {
        if (Settings.canDrawOverlays(this)) {
            ContextCompat.startForegroundService(this, Intent(this, OverlayService::class.java))
            Toast.makeText(this, "HUD Overlay запущен", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Сначала дай разрешение Overlay", Toast.LENGTH_LONG).show()
        }
    }

    fun stopOverlay() {
        stopService(Intent(this, OverlayService::class.java))
        Toast.makeText(this, "HUD Overlay остановлен", Toast.LENGTH_SHORT).show()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1001 && resultCode == RESULT_OK) {
            startService(Intent(this, VpnDnsService::class.java))
            Toast.makeText(this, "VPN DNS Booster запущен", Toast.LENGTH_SHORT).show()
        }
    }
}
