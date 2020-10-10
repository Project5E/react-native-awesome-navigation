package com.reactnativepurenavigation

import android.R
import androidx.fragment.app.Fragment
import com.facebook.common.logging.FLog
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap


class ALCNavigationBridge(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "ALCNavigationBridge"
    }

    @ReactMethod
    fun setRoot(a: ReadableMap) {
      val activity = currentActivity
      val viewFragment: Fragment = HelloFragment()
      activity.beginTransaction().add(R.id.container, viewFragment).commit()

      FLog.w("----", "setRoot.");
    }

    @ReactMethod
    fun signalFirstRenderComplete(screenID: String) {
      FLog.w("----", screenID);
    }

    @ReactMethod
    fun registerReactComponent(appKey: String, options: ReadableMap) {
      FLog.w("----", "registerReactComponent.");
    }

    @ReactMethod
    fun setStyle(style: ReadableMap) {
      FLog.w("----", "setStyle.");
    }
}
