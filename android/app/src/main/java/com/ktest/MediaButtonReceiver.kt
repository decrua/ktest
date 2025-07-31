package com.ktest

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.view.KeyEvent

class MediaButtonReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (Intent.ACTION_MEDIA_BUTTON == intent.action) {
            val keyEvent = intent.getParcelableExtra<KeyEvent>(Intent.EXTRA_KEY_EVENT)
            if (keyEvent?.action == KeyEvent.ACTION_DOWN) {
                // Просто перенаправляем событие в нашу службу для обработки
                val serviceIntent = Intent(context, MediaKeyService::class.java)
                serviceIntent.action = Intent.ACTION_MEDIA_BUTTON
                serviceIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent)
                context.startService(serviceIntent)
            }
        }
    }
}
