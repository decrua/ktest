package com.ktest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule

class MediaKeyService : Service() {

    private lateinit var mediaSession: MediaSession
    private val uiHandler = Handler(Looper.getMainLooper())

    companion object {
        var reactContext: ReactApplicationContext? = null
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        showToast("7: Служба MediaKeyService создана")

        mediaSession = MediaSession(this, "ktestMediaSessionService")
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)

        val state = PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY_PAUSE or PlaybackState.ACTION_PLAY)
            .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
            .build()
        mediaSession.setPlaybackState(state)

        mediaSession.setCallback(object : MediaSession.Callback() {
            override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
                val keyEvent = mediaButtonEvent.getParcelableExtra<android.view.KeyEvent>(Intent.EXTRA_KEY_EVENT)
                if (keyEvent != null && keyEvent.action == android.view.KeyEvent.ACTION_DOWN) {
                    if (keyEvent.keyCode == 79) {
                        showToast("5: Кнопка 79 нажата (из службы)!")
                        sendEvent("onMediaKey79Pressed", null)
                    }
                }
                return super.onMediaButtonEvent(mediaButtonEvent)
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        showToast("8: Служба запущена")

        val channelId = "MediaKeyChannel"
        val channel = NotificationChannel(channelId, "Media Key Listener", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("ktest работает")
            .setContentText("Отслеживание медиа-кнопок активно")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(1, notification)

        mediaSession.isActive = true

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        showToast("13: Служба остановлена")
        mediaSession.release()
    }

    private fun sendEvent(eventName: String, params: Any?) {
        reactContext?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            ?.emit(eventName, params)
    }

    private fun showToast(message: String) {
        uiHandler.post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}