package com.ktest

import android.content.Intent
import android.os.Build
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class MediaKeyListenerModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    init {
        MediaKeyService.reactContext = reactContext
    }

    override fun getName() = "MediaKeyListener"

    @ReactMethod
    fun start() {
        Toast.makeText(reactContext, "4: Запрос на запуск службы", Toast.LENGTH_SHORT).show()
        val intent = Intent(reactContext, MediaKeyService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            reactContext.startForegroundService(intent)
        } else {
            reactContext.startService(intent)
        }
    }

    @ReactMethod
    fun stop() {
        Toast.makeText(reactContext, "6: Запрос на остановку службы", Toast.LENGTH_SHORT).show()
        val intent = Intent(reactContext, MediaKeyService::class.java)
        reactContext.stopService(intent)
    }

    @ReactMethod
    fun addListener(eventName: String) {}

    @ReactMethod
    fun removeListeners(count: Int) {}
}