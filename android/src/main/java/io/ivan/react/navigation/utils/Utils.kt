package io.ivan.react.navigation.utils

import android.content.Context
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.modules.core.DeviceEventManagerModule


val Context.reactNativeHost: ReactNativeHost get() = (applicationContext as ReactApplication).reactNativeHost

/**
 * @param data Types:
 *
 * Boolean -> Bool
 * Integer -> Number
 * Double -> Number
 * Float -> Number
 * String -> String
 * Callback -> function
 * ReadableMap -> Object
 * ReadableArray -> Array
 */
fun Context.sendEvent(eventName: String, data: Any?) {
    reactNativeHost.reactInstanceManager.currentReactContext
        ?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        ?.emit(eventName, data)
}
