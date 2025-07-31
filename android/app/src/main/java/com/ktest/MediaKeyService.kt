package com.ktest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.KeyEvent
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule
// Удаляем ненужные импорты аудиофокуса, чтобы упростить код

class MediaKeyService : Service() {

    private lateinit var mediaSession: MediaSession
    private val uiHandler = Handler(Looper.getMainLooper())

    companion object {
        var reactContext: ReactApplicationContext? = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        showToast("7: Служба MediaKeyService создана")

        val mediaButtonReceiver = ComponentName(applicationContext, MediaButtonReceiver::class.java)
        mediaSession = MediaSession(applicationContext, "ktestMediaSessionService")

        // Создаем PendingIntent, который указывает на наш новый Receiver
        val mediaButtonIntent = Intent(Intent.ACTION_MEDIA_BUTTON).apply {
            setClass(applicationContext, MediaButtonReceiver::class.java)
        }
        val pendingIntent = PendingIntent.getBroadcast(applicationContext, 0, mediaButtonIntent, PendingIntent.FLAG_IMMUTABLE)
        mediaSession.setMediaButtonReceiver(pendingIntent) // <-- Вот оно! Связываем сессию и ресивер.

        val state = PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY_PAUSE or PlaybackState.ACTION_PLAY)
            .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
            .build()
        mediaSession.setPlaybackState(state)

        // Этот колбэк больше не нужен, т.к. события обрабатывает MediaButtonReceiver
        // mediaSession.setCallback(...)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Проверяем, пришло ли событие от нашего ресивера
        if (intent?.action == Intent.ACTION_MEDIA_BUTTON) {
            val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            if (keyEvent?.keyCode == 79) {
                showToast("5: Кнопка 79 нажата (обработано в службе)!")
                sendEvent("onMediaKey79Pressed", null)
            }
            return START_STICKY // Просто обрабатываем и выходим
        }

        showToast("8: Служба запущена")
        
        // Активируем сессию, чтобы система знала, что мы главный плеер
        mediaSession.isActive = true

        // Создаем уведомление и запускаем службу в режиме Foreground
        val channelId = "MediaKeyChannel"
        val channel = NotificationChannel(channelId, "Media Key Listener", NotificationManager.IMPORTANCE_LOW)
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)

        val notification = Notification.Builder(this, channelId)
            .setContentTitle("ktest работает")
            .setContentText("Отслеживание медиа-кнопок активно")
            .setSmallIcon(R.mipmap.ic_launcher)
            .build()

        startForeground(1, notification)

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        showToast("13: Служба остановлена")
        mediaSession.release()
    }

    private fun sendEvent(eventName: String, params: Any?) {
        reactContext
            ?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            ?.emit(eventName, params)
    }

    private fun showToast(message: String) {
        uiHandler.post {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
