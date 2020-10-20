package io.ivan.react.navigation.bridge

import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule

class ALCConstants(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

    override fun getName(): String {
        return "ALCConstants"
    }

    override fun getConstants(): MutableMap<String, Any> {
        val constants = HashMap<String, Any>()
        constants["NAVIGATION_EVENT"] = "NAVIGATION_EVENT"
        constants["EVENT_TYPE"] = "EVENT_TYPE"
        constants["RECLICK_TAB"] = "RECLICK_TAB"
        constants["VIEW_DID_APPEAR"] = "VIEW_DID_APPEAR"
        constants["VIEW_DID_DISAPPEAR"] = "VIEW_DID_DISAPPEAR"
        constants["COMPONENT_RESULT"] = "COMPONENT_RESULT"
        constants["RESULT_TYPE"] = "RESULT_TYPE"
        constants["RESULT_TYPE_OK"] = "RESULT_TYPE_OK"
        constants["RESULT_TYPE_CANCEL"] = "RESULT_TYPE_CANCEL"
        constants["RESULT_DATA"] = "RESULT_DATA"
        constants["SCREEN_ID"] = "SCREEN_ID"
        return constants
    }

}
