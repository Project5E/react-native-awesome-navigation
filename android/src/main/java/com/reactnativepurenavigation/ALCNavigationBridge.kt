package com.reactnativepurenavigation

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
      val fragment : FragmentName = FragmentName.newInstance()
      activity.
      FLog.w("asd", "View hierarchy is not ready now.");
    }

    @ReactMethod
    fun registerReactComponent(appKey: String, options: ReadableMap) {
      FLog.w("asd", "View hierarchy is not ready now.");
    }
}
