package io.ivan.react.navigation.utils

import com.facebook.react.bridge.ReadableArray
import com.facebook.react.bridge.ReadableMap

fun ReadableMap.optBoolean(name: String): Boolean? {
    return if (hasKey(name) && !(isNull(name))) {
        getBoolean(name)
    } else {
        null
    }
}

fun ReadableMap.optDouble(name: String): Double? {
    return if (hasKey(name) && !(isNull(name))) {
        getDouble(name)
    } else {
        null
    }
}

fun ReadableMap.optInt(name: String): Int? {
    return if (hasKey(name) && !(isNull(name))) {
        getInt(name)
    } else {
        null
    }
}

fun ReadableMap.optString(name: String): String? {
    return if (hasKey(name)) {
        getString(name)
    } else {
        null
    }
}

fun ReadableMap.optArray(name: String): ReadableArray? {
    return if (hasKey(name)) {
        getArray(name)
    } else {
        null
    }
}

fun ReadableMap.optMap(name: String): ReadableMap? {
    return if (hasKey(name)) {
        getMap(name)
    } else {
        null
    }
}
