package io.ivan.react.navigation.utils

import android.content.Context
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.modules.core.DeviceEventManagerModule
import io.ivan.react.navigation.bridge.NavigationConstants
import org.json.JSONObject


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

fun Context.sendNavigationEvent(eventType: String, screenId: String?, data: Any? = null, resultType: String? = null) {
    reactNativeHost.reactInstanceManager.currentReactContext
        ?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
        ?.emit(NavigationConstants.NAVIGATION_EVENT, JSONObject().also { args ->
            args.put(NavigationConstants.EVENT_TYPE, eventType)
            args.putOpt(NavigationConstants.SCREEN_ID, screenId)
            args.putOpt(NavigationConstants.RESULT_DATA, data)
            args.putOpt(NavigationConstants.RESULT_TYPE, resultType)
        }.toRNMap())
}
