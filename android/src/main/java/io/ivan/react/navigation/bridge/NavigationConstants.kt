package io.ivan.react.navigation.bridge

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

class NavigationConstants(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    companion object {
        const val NAVIGATION_EVENT = "NAVIGATION_EVENT"
        const val EVENT_TYPE = "EVENT_TYPE"
        const val RECLICK_TAB = "RECLICK_TAB"
        const val VIEW_DID_APPEAR = "VIEW_DID_APPEAR"
        const val VIEW_DID_DISAPPEAR = "VIEW_DID_DISAPPEAR"
        const val COMPONENT_RESULT = "COMPONENT_RESULT"
        const val RESULT_TYPE = "RESULT_TYPE"
        const val RESULT_TYPE_OK = "RESULT_TYPE_OK"
        const val RESULT_TYPE_CANCEL = "RESULT_TYPE_CANCEL"
        const val RESULT_DATA = "RESULT_DATA"
        const val SCREEN_ID = "SCREEN_ID"
    }

    override fun getName(): String {
        return "ALCConstants"
    }

    override fun getConstants(): MutableMap<String, Any> {
        return mutableMapOf<String, Any>().also {
            it[NAVIGATION_EVENT] = NAVIGATION_EVENT
            it[EVENT_TYPE] = EVENT_TYPE
            it[RECLICK_TAB] = RECLICK_TAB
            it[VIEW_DID_APPEAR] = VIEW_DID_APPEAR
            it[VIEW_DID_DISAPPEAR] = VIEW_DID_DISAPPEAR
            it[COMPONENT_RESULT] = COMPONENT_RESULT
            it[RESULT_TYPE] = RESULT_TYPE
            it[RESULT_TYPE_OK] = RESULT_TYPE_OK
            it[RESULT_TYPE_CANCEL] = RESULT_TYPE_CANCEL
            it[RESULT_DATA] = RESULT_DATA
            it[SCREEN_ID] = SCREEN_ID
        }
    }

}
