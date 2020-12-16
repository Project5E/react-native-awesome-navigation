package io.ivan.react.navigation

import com.facebook.react.bridge.*
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.facebook.react.uimanager.events.RCTEventEmitter
import io.ivan.react.navigation.NavigationManager.reactInstanceManager

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
        sendEvent(
            NavigationConstants.NAVIGATION_EVENT,
            Arguments.createMap().also { map ->
                map.putString(NavigationConstants.EVENT_TYPE, eventType)

                screenId?.let {
                    map.putString(NavigationConstants.SCREEN_ID, it)
                } ?: map.putNull(NavigationConstants.SCREEN_ID)

                with(NavigationConstants.RESULT_DATA) {
                    when (data) {
                        data == null -> {
                            map.putNull(this)
                        }
                        is Boolean -> {
                            map.putBoolean(this, data)
                        }
                        is Int -> {
                            map.putInt(this, data)
                        }
                        is Double -> {
                            map.putDouble(this, data)
                        }
                        is String -> {
                            map.putString(this, data)
                        }
                        is ReadableMap -> {
                            map.putMap(this, data)
                        }
                        is ReadableArray -> {
                            map.putArray(this, data)
                        }
                        else -> {
                            map.putString(this, data.toString())
                        }
                    }
                }
            }
        )
    }
}