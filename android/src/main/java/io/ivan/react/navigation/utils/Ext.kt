package io.ivan.react.navigation.utils

import android.os.Bundle
import com.facebook.react.bridge.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

fun ReadableMap?.toBundle(): Bundle? {
    return Arguments.toBundle(this)
}

fun Bundle?.toRNMap(): WritableMap? {
    return Arguments.fromBundle(this)
}

fun ReadableMap?.toJSONObject(): JSONObject? {
    this ?: return null

    return try {
        val iterator = keySetIterator()
        val result = JSONObject()
        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()
            when (getType(key)) {
                ReadableType.String -> result.put(key, getString(key))
                ReadableType.Number -> result.put(key, parseNumber(this, key))
                ReadableType.Boolean -> result.put(key, getBoolean(key))
                ReadableType.Array -> result.put(key, getArray(key)?.toJSONObject())
                ReadableType.Map -> result.put(key, getMap(key)?.toJSONObject())
                else -> {
                }
            }
        }
        result
    } catch (e: JSONException) {
        throw RuntimeException(e)
    }
}

fun JSONObject?.toRNMap(): WritableMap? {
    this ?: return null

    val map: WritableMap = WritableNativeMap()
    val iterator = keys()
    while (iterator.hasNext()) {
        val key = iterator.next()
        when (val value = opt(key)) {
            is JSONObject -> {
                map.putMap(key, value.toRNMap())
            }
            is JSONArray -> {
                map.putArray(key, value.toRNArray())
            }
            is Boolean -> {
                map.putBoolean(key, value)
            }
            is Int -> {
                map.putInt(key, value)
            }
            is Double -> {
                map.putDouble(key, value)
            }
            is String -> {
                map.putString(key, value)
            }
            else -> {
                map.putString(key, value!!.toString())
            }
        }
    }
    return map
}

fun Array<Any>?.toRNArray(): WritableArray? {
    return this?.let { Arguments.fromArray(it) }
}

fun List<Any>?.toRNArray(): WritableArray? {
    return this?.let { Arguments.fromList(it) }
}

fun ReadableArray?.toJSONObject(): JSONArray? {
    this ?: return null

    val result = JSONArray()
    for (i in 0 until size()) {
        when (getType(i)) {
            ReadableType.String -> result.put(getString(i))
            ReadableType.Number -> result.put(
                parseNumber(this, i)
            )
            ReadableType.Boolean -> result.put(getBoolean(i))
            ReadableType.Array -> result.put(getArray(i)!!.toJSONObject())
            ReadableType.Map -> result.put(getMap(i)!!.toJSONObject())
            else -> {
            }
        }
    }
    return result
}


fun JSONArray?.toRNArray(): WritableArray? {
    this ?: return null

    val array: WritableArray = WritableNativeArray()
    for (i in 0 until length()) {
        when (val value = opt(i)) {
            is JSONObject -> {
                array.pushMap(value.toRNMap())
            }
            is JSONArray -> {
                array.pushArray(value.toRNArray())
            }
            is Boolean -> {
                array.pushBoolean(value)
            }
            is Int -> {
                array.pushInt(value)
            }
            is Double -> {
                array.pushDouble(value)
            }
            is String -> {
                array.pushString(value)
            }
            else -> {
                array.pushString(value!!.toString())
            }
        }
    }
    return array
}

private fun parseNumber(map: ReadableMap, key: String): Any {
    return try {
        val doubleValue = map.getDouble(key)
        if (doubleValue % 1 == 0.0) {
            map.getInt(key)
        } else doubleValue
    } catch (e: Exception) {
        map.getInt(key)
    }
}

private fun parseNumber(arr: ReadableArray, index: Int): Any {
    return try {
        val doubleValue = arr.getDouble(index)
        if (doubleValue % 1 == 0.0) {
            arr.getInt(index)
        } else doubleValue
    } catch (e: Exception) {
        arr.getInt(index)
    }
}
