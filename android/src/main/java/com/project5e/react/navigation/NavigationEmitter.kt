package com.project5e.react.navigation

import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.uimanager.events.RCTEventEmitter
import com.project5e.react.navigation.NavigationConstants.Companion.EVENT_TYPE
import com.project5e.react.navigation.NavigationConstants.Companion.NAVIGATION_EVENT
import com.project5e.react.navigation.NavigationConstants.Companion.RESULT_DATA
import com.project5e.react.navigation.NavigationConstants.Companion.SCREEN_ID
import com.project5e.react.navigation.NavigationManager.reactInstanceManager

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
object NavigationEmitter {

    @JvmStatic
    fun sendEvent(eventName: String, data: Any?) {
        reactInstanceManager.currentReactContext
            ?.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
            ?.emit(eventName, data)
    }

    @JvmStatic
    fun receiveEvent(targetTag: Int, eventName: String, event: WritableMap?) {
        reactInstanceManager.currentReactContext
            ?.getJSModule(RCTEventEmitter::class.java)
            ?.receiveEvent(targetTag, eventName, event)
    }

    @JvmStatic
    fun receiveTouches(eventName: String, touches: WritableArray, changedIndices: WritableArray) {
        reactInstanceManager.currentReactContext
            ?.getJSModule(RCTEventEmitter::class.java)
            ?.receiveTouches(eventName, touches, changedIndices)
    }

    @JvmStatic
    fun sendNavigationEvent(eventType: String, screenId: String?, data: Any? = null) {
        sendEvent(NAVIGATION_EVENT, Arguments.createMap().also { map ->
            map.putString(EVENT_TYPE, eventType)

            screenId?.let { map.putString(SCREEN_ID, it) } ?: map.putNull(SCREEN_ID)

            with(RESULT_DATA) {
                when (data) {
                    data == null -> map.putNull(this)
                    is Boolean -> map.putBoolean(this, data)
                    is Int -> map.putInt(this, data)
                    is Double -> map.putDouble(this, data)
                    is String -> map.putString(this, data)
                    is ReadableMap -> map.putMap(this, data)
                    is ReadableArray -> map.putArray(this, data)
                    else -> map.putString(this, data.toString())
                }
            }
        })
    }
}