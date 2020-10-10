package com.reactnativepurenavigation

import com.facebook.common.logging.FLog
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap

class ALCConstants(reactContext: ReactApplicationContext) : ReactContextBaseJavaModule(reactContext) {

  override fun getName(): String {
    return "ALCConstants"
  }

  override fun getConstants(): MutableMap<String, Any> {
    val constants = HashMap<String, Any>()
    constants.put("NAVIGATION_EVENT", "NAVIGATION_EVENT")
    constants.put("EVENT_TYPE", "EVENT_TYPE")
    constants.put("RECLICK_TAB", "RECLICK_TAB")
    constants.put("VIEW_DID_APPEAR", "VIEW_DID_APPEAR")
    constants.put("VIEW_DID_DISAPPEAR", "VIEW_DID_DISAPPEAR")
    constants.put("COMPONENT_RESULT", "COMPONENT_RESULT")
    constants.put("RESULT_TYPE", "RESULT_TYPE")
    constants.put("RESULT_TYPE_OK", "RESULT_TYPE_OK")
    constants.put("RESULT_TYPE_CANCEL", "RESULT_TYPE_CANCEL")
    constants.put("RESULT_DATA", "RESULT_DATA")
    constants.put("SCREEN_ID", "SCREEN_ID")
    return constants
  }

}
