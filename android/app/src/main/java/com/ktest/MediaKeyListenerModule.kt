package com.ktest

import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class MediaKeyListenerModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    init {
        // Передаем контекст для отправки событий в JS
        MediaKeyService.reactContext = reactContext
    }

    override fun getName() = "MediaKeyListener"

    @ReactMethod
    fun start() {
        Toast.makeText(reactContext, "4: Запрос на запуск службы", Toast.LENGTH_SHORT).show()
        // Используем applicationContext - это более надежно для запуска служб
        val appContext = reactContext.applicationContext
        val intent = Intent(appContext, MediaKeyService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            appContext.startForegroundService(intent)
        } else {
            appContext.startService(intent)
        }
    }

    @ReactMethod
    fun stop() {
        Toast.makeText(reactContext, "6: Запрос на остановку службы", Toast.LENGTH_SHORT).show()
        val appContext = reactContext.applicationContext
        val intent = Intent(appContext, MediaKeyService::class.java)
        appContext.stopService(intent)
    }

    // Методы для NativeEventEmitter
    @ReactMethod
    fun addListener(eventName: String) {}

    @ReactMethod
    fun removeListeners(count: Int) {}
}
