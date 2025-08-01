package com.ktest

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod

class MediaKeyListenerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    init {
        // Передаем контекст в службу, это остается важным
        MediaKeyService.reactContext = reactContext
    }

    override fun getName() = "MediaKeyListener"

    // Эти методы теперь пустые, но они нужны для React Native
    @ReactMethod
    fun start() {}

    @ReactMethod
    fun stop() {}

    @ReactMethod
    fun addListener(eventName: String) {}

    @ReactMethod
    fun removeListeners(count: Int) {}
}
