package com.ktest

import android.os.Handler
import android.os.Looper
import android.widget.Toast
import android.view.View
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ReactShadowNode
import com.facebook.react.uimanager.ViewManager

class MediaKeyListenerPackage : ReactPackage {
    override fun createViewManagers(
        reactContext: ReactApplicationContext
    ): MutableList<ViewManager<View, ReactShadowNode<*>>> = mutableListOf()

    override fun createNativeModules(
        reactContext: ReactApplicationContext
    ): MutableList<NativeModule> {
        // Используем Handler для UI-потока
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(
                reactContext, 
                "9: Создание нативного модуля", 
                Toast.LENGTH_SHORT
            ).show()
        }
        return listOf(MediaKeyListenerModule(reactContext)).toMutableList()
    }
}