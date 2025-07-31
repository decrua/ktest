package com.ktest

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.session.MediaSession
import android.media.session.PlaybackState
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.modules.core.DeviceEventManagerModule

class MediaKeyService : Service(), AudioManager.OnAudioFocusChangeListener {

    private lateinit var mediaSession: MediaSession
    private lateinit var audioManager: AudioManager
    private var audioFocusRequest: AudioFocusRequest? = null
    private val uiHandler = Handler(Looper.getMainLooper())

    companion object {
        var reactContext: ReactApplicationContext? = null
    }

    override fun onBind(intent: Intent?): IBinder? = null

    // Метод для обработки изменений аудиофокуса
    override fun onAudioFocusChange(focusChange: Int) {
        // Здесь можно обрабатывать потерю фокуса, но для нашей задачи это не критично
        showToast("Статус аудиофокуса изменился: $focusChange")
    }

    override fun onCreate() {
        super.onCreate()
        showToast("7: Служба MediaKeyService создана")

        audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager

        mediaSession = MediaSession(this, "ktestMediaSessionService")
        // ... остальная настройка mediaSession ...
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS or MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS)

        val state = PlaybackState.Builder()
            .setActions(PlaybackState.ACTION_PLAY_PAUSE or PlaybackState.ACTION_PLAY)
            .setState(PlaybackState.STATE_PLAYING, 0, 1.0f)
            .build()
        mediaSession.setPlaybackState(state)

        mediaSession.setCallback(object : MediaSession.Callback() {
            override fun onMediaButtonEvent(mediaButtonEvent: Intent): Boolean {
                showToast("Событие медиа-кнопки получено!") // <-- Важный тост для отладки
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
        
        // 1. Запрашиваем аудиофокус
        val result = requestAudioFocus()

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            showToast("Аудиофокус получен!")
            // 2. Активируем сессию ТОЛЬКО после получения фокуса
            mediaSession.isActive = true
        } else {
            showToast("Не удалось получить аудиофокус.")
        }

        // 3. Создаем уведомление и запускаем службу в режиме Foreground
        // ... код создания уведомления без изменений ...
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
        abandonAudioFocus() // Освобождаем аудиофокус
        mediaSession.release()
    }

    private fun requestAudioFocus(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                .setAudioAttributes(attributes)
                .setAcceptsDelayedFocusGain(true)
                .setOnAudioFocusChangeListener(this)
                .build()
            audioFocusRequest?.let { audioManager.requestAudioFocus(it) } ?: AudioManager.AUDIOFOCUS_REQUEST_FAILED
        } else {
            @Suppress("DEPRECATION")
            audioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)
        }
    }
    
    private fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
        } else {
            @Suppress("DEPRECATION")
            audioManager.abandonAudioFocus(this)
        }
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
