package com.ktest

import android.app.Application
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import com.facebook.react.PackageList
import com.facebook.react.ReactApplication
import com.facebook.react.ReactHost
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactPackage
import com.facebook.react.defaults.DefaultNewArchitectureEntryPoint.loadNamedLibrary
import com.facebook.react.defaults.DefaultReactHost.getDefaultReactHost
import com.facebook.react.defaults.DefaultReactNativeHost
import com.facebook.soloader.SoLoader

class MainApplication : Application(), ReactApplication {

  override val reactNativeHost: ReactNativeHost =
      object : DefaultReactNativeHost(this) {
        override fun getPackages(): List<ReactPackage> {
          // Используем Handler для UI-потока
          Handler(Looper.getMainLooper()).post {
              Toast.makeText(
                  applicationContext,
                  "1: Инициализация пакетов",
                  Toast.LENGTH_SHORT
              ).show()
          }
          return PackageList(this).packages.apply {
            add(MediaKeyListenerPackage())
          }
        }

        override fun getJSMainModuleName(): String = "index"

        override fun getUseDeveloperSupport(): Boolean = BuildConfig.DEBUG

        override val isNewArchEnabled: Boolean = BuildConfig.IS_NEW_ARCHITECTURE_ENABLED

        override val isHermesEnabled: Boolean = BuildConfig.IS_HERMES_ENABLED
      }

  override val reactHost: ReactHost
    get() = getDefaultReactHost(this.applicationContext, reactNativeHost)

  override fun onCreate() {
    super.onCreate()
    Toast.makeText(this, "2: Приложение запущено", Toast.LENGTH_SHORT).show()
    SoLoader.init(this, false)
    if (BuildConfig.IS_NEW_ARCHITECTURE_ENABLED) {
      loadNamedLibrary("react_newarchdefaults")
    }
    
    // ЗАПУСКАЕМ СЛУЖБУ ЗДЕСЬ!
    startMediaKeyService()
  }

  private fun startMediaKeyService() {
      val serviceIntent = Intent(this, MediaKeyService::class.java)
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
          startForegroundService(serviceIntent)
      } else {
          startService(serviceIntent)
      }
  }
}
