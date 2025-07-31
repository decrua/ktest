package com.ktest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

class MainActivity : ReactActivity() {

    // Наш приемник команд
    private val serviceCommandReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                MediaKeyListenerModule.ACTION_START_SERVICE -> {
                    val serviceIntent = Intent(applicationContext, MediaKeyService::class.java)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(serviceIntent)
                    } else {
                        startService(serviceIntent)
                    }
                }
                MediaKeyListenerModule.ACTION_STOP_SERVICE -> {
                    val serviceIntent = Intent(applicationContext, MediaKeyService::class.java)
                    stopService(serviceIntent)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Регистрируем наш приемник
        val filter = IntentFilter().apply {
            addAction(MediaKeyListenerModule.ACTION_START_SERVICE)
            addAction(MediaKeyListenerModule.ACTION_STOP_SERVICE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(serviceCommandReceiver, filter, RECEIVER_EXPORTED)
        } else {
            registerReceiver(serviceCommandReceiver, filter)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Обязательно отменяем регистрацию приемника
        unregisterReceiver(serviceCommandReceiver)
    }

    override fun getMainComponentName(): String = "ktest"

    override fun createReactActivityDelegate(): ReactActivityDelegate =
        DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)
}
