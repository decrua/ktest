package com.ktest

import android.content.Context
import android.media.session.MediaSession
import android.media.session.PlaybackState
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule

class MediaKeyListenerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private lateinit var mediaSession: MediaSession

    override fun getName() = "MediaKeyListener"

    @ReactMethod
    fun start() {
        // Уже в UI потоке благодаря React Native
        mediaSession = MediaSession(reactApplicationContext, "ktestMediaSession")

        // Устанавливаем флаги
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)

        // Устанавливаем состояние воспроизведения
        val state = PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY_PAUSE or PlaybackState.ACTION_PLAY)
            .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
            .build()
        mediaSession.setPlaybackState(state)

        // Устанавливаем колбэк для обработки медиа-кнопок
        mediaSession.setCallback(object : MediaSession.Callback() {
            override fun onMediaButtonEvent(mediaButtonEvent: android.content.Intent): Boolean {
                val keyEvent = mediaButtonEvent.getParcelableExtra<android.view.KeyEvent>(android.content.Intent.EXTRA_KEY_EVENT)
                if (keyEvent != null && keyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                    // Проверяем код кнопки (79 - KEYCODE_MEDIA_PLAY_PAUSE)
                    if (keyEvent.keyCode == 79) {
                        sendEvent("onMediaKey79Pressed", null)
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent)
            }
        })

        // Активируем сессию
        mediaSession.isActive = true
    }

    // Метод для отправки событий в JavaScript
    private fun sendEvent(eventName: String, params: Any?) {
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    @ReactMethod
    fun addListener(eventName: String) {
        // Обязательный метод для Event Emitter
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        // Обязательный метод для Event Emitter
    }
}
