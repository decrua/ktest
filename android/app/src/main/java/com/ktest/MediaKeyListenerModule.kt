package com.ktest

import android.content.Intent
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class MediaKeyListenerModule(private val reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    // Определяем константы для наших команд
    companion object {
        const val ACTION_START_SERVICE = "com.ktest.ACTION_START_SERVICE"
        const val ACTION_STOP_SERVICE = "com.ktest.ACTION_STOP_SERVICE"
    }

    init {
        MediaKeyService.reactContext = reactContext
    }

    override fun getName() = "MediaKeyListener"

    @ReactMethod
    fun start() {
        Toast.makeText(reactContext, "4: Отправка команды на запуск службы", Toast.LENGTH_SHORT).show()
        val intent = Intent(ACTION_START_SERVICE)
        reactContext.sendBroadcast(intent)
    }

    @ReactMethod
    fun stop() {
        Toast.makeText(reactContext, "6: Отправка команды на остановку службы", Toast.LENGTH_SHORT).show()
        val intent = Intent(ACTION_STOP_SERVICE)
        reactContext.sendBroadcast(intent)
    }

    @ReactMethod
    fun addListener(eventName: String) {}

    @ReactMethod
    fun removeListeners(count: Int) {}
}
