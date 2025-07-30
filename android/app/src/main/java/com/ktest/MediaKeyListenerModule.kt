package com.ktest

import android.content.Context
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule

class MediaKeyListenerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private lateinit var mediaSession: MediaSession

    init {
        // [3] Toast при создании модуля
        Toast.makeText(reactContext, "3: Модуль MediaKey создан", Toast.LENGTH_SHORT).show()
    }

    override fun getName() = "MediaKeyListener"

    @ReactMethod
    fun start() {
        // [4] Toast при запуске прослушивания
        Toast.makeText(reactApplicationContext, "4: Прослушивание запущено", Toast.LENGTH_SHORT).show()
        
        mediaSession = MediaSession(reactApplicationContext, "ktestMediaSession")
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)

        val state = PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY_PAUSE or PlaybackState.ACTION_PLAY)
            .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
            .build()
        mediaSession.setPlaybackState(state)

        mediaSession.setCallback(object : MediaSession.Callback() {
            override fun onMediaButtonEvent(mediaButtonEvent: android.content.Intent): Boolean {
                val keyEvent = mediaButtonEvent.getParcelableExtra<android.view.KeyEvent>(android.content.Intent.EXTRA_KEY_EVENT)
                if (keyEvent != null && keyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                    if (keyEvent.keyCode == 79) {
                        // [5] Toast при обнаружении нажатия
                        Toast.makeText(
                            reactApplicationContext, 
                            "5: Кнопка 79 нажата!", 
                            Toast.LENGTH_SHORT
                        ).show()
                        sendEvent("onMediaKey79Pressed", null)
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent)
            }
        })
        mediaSession.isActive = true
    }

    private fun sendEvent(eventName: String, params: Any?) {
        // [6] Toast при отправке события в JS
        Toast.makeText(
            reactApplicationContext, 
            "6: Событие $eventName отправлено", 
            Toast.LENGTH_SHORT
        ).show()
        
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    @ReactMethod
    fun addListener(eventName: String) {
        Toast.makeText(
            reactApplicationContext, 
            "7: Слушатель $eventName добавлен", 
            Toast.LENGTH_SHORT
        ).show()
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        Toast.makeText(
            reactApplicationContext, 
            "8: Удалено $count слушателей", 
            Toast.LENGTH_SHORT
        ).show()
    }
}