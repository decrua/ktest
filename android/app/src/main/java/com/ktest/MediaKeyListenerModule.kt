package com.ktest

import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class MediaKeyListenerModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    init {
        // Передаем контекст в статическое поле сервиса
        MediaKeyService.reactContext = reactContext
    }

    override fun getName() = "MediaKeyListener"

    @ReactMethod
    fun start() {
        Toast.makeText(reactContext, "4: Запрос на запуск службы", Toast.LENGTH_SHORT).show()
        val intent = Intent(reactContext, MediaKeyService::class.java)
        // Для Android O (8.0) и выше используем startForegroundService
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            reactContext.startForegroundService(intent)
        } else {
            reactContext.startService(intent)
        }
    }

    @ReactMethod
    fun stop() {
        Toast.makeText(reactContext, "Запрос на остановку службы", Toast.LENGTH_SHORT).show()
        val intent = Intent(reactContext, MediaKeyService::class.java)
        reactContext.stopService(intent)
    }

    // Эти методы нужны для NativeEventEmitter
    @ReactMethod
    fun addListener(eventName: String) {}

    @ReactMethod
    fun removeListeners(count: Int) {}
}
