package com.ktest

import android.content.Context
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule

class MediaKeyListenerModule(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    private lateinit var mediaSession: MediaSession
    private val uiHandler = Handler(Looper.getMainLooper())

    init {
        showToast("3: Модуль MediaKey создан")
    }

    override fun getName() = "MediaKeyListener"

    @ReactMethod
    fun start() {
        showToast("4: Прослушивание запущено")
        
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
                        showToast("5: Кнопка 79 нажата!")
                        sendEvent("onMediaKey79Pressed", null)
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent)
            }
        })
        mediaSession.isActive = true
    }

    private fun sendEvent(eventName: String, params: Any?) {
        showToast("6: Событие $eventName отправлено")
        reactApplicationContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            .emit(eventName, params)
    }

    @ReactMethod
    fun addListener(eventName: String) {
        showToast("7: Слушатель $eventName добавлен")
    }

    @ReactMethod
    fun removeListeners(count: Int) {
        showToast("8: Удалено $count слушателей")
    }

    // Показ Toast в UI потоке
    private fun showToast(message: String) {
        uiHandler.post {
            Toast.makeText(
                reactApplicationContext, 
                message, 
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}