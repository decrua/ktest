package com.ktest

import android.content.Intent
import android.os.Build
import android.os.Bundle // <-- Добавьте этот импорт
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.fabricEnabled
import com.facebook.react.defaults.DefaultReactActivityDelegate

class MainActivity : ReactActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState) // <-- Обязательно вызовите super.onCreate
    
    // ЗАПУСКАЕМ СЛУЖБУ ЗДЕСЬ
    val serviceIntent = Intent(this, MediaKeyService::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        startForegroundService(serviceIntent)
    } else {
        startService(serviceIntent)
    }
  }

  override fun getMainComponentName(): String = "ktest"

  override fun createReactActivityDelegate(): ReactActivityDelegate =
      DefaultReactActivityDelegate(this, mainComponentName, fabricEnabled)
}
