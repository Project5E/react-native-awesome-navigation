package com.project5e.react.navigation.utils

import com.facebook.react.bridge.*
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject

val gson = Gson()

inline fun <reified T> parse(map: ReadableMap?): T? {
    map ?: return null

    val json = parse(map).toString()
    return gson.fromJson(json, T::class.java)
}

inline fun <reified T> parse(arr: ReadableArray?): T? {
    arr ?: return null

    val json = parse(arr).toString()
    return gson.fromJson(json, T::class.java)
}

inline fun <reified T> convert(t: T?): WritableMap {
    t ?: return Arguments.createMap()

    val json = gson.toJson(t)
    return convert(JSONObject(json))
}

inline fun <reified T> convertArray(t: Array<T?>?): WritableArray {
    t ?: return Arguments.createArray()

    val json = gson.toJson(t)
    return convert(JSONArray(json))
}

fun parse(map: ReadableMap?): JSONObject {
    val result = JSONObject()
    map ?: return result

    val it = map.keySetIterator()
    while (it.hasNextKey()) {
        val key = it.nextKey()
        when (map.getType(key)) {
            ReadableType.String -> result.put(key, map.getString(key))
            ReadableType.Number -> result.put(key, parseNumber(map, key))
            ReadableType.Boolean -> result.put(key, map.getBoolean(key))
            ReadableType.Array -> result.put(key, parse(map.getArray(key)))
            ReadableType.Map -> result.put(key, parse(map.getMap(key)))
            else -> {
            }
        }
    }
    return result
}

fun parse(arr: ReadableArray?): JSONArray {
    val result = JSONArray()
    arr ?: return result

    for (i in 0 until arr.size()) {
        when (arr.getType(i)) {
            ReadableType.String -> result.put(arr.getString(i))
            ReadableType.Number -> result.put(parseNumber(arr, i))
            ReadableType.Boolean -> result.put(arr.getBoolean(i))
            ReadableType.Array -> result.put(parse(arr.getArray(i)))
            ReadableType.Map -> result.put(parse(arr.getMap(i)))
            else -> {
            }
        }
    }
    return result
}

fun convert(jsonObject: JSONObject): WritableMap {
    val map: WritableMap = WritableNativeMap()
    val iterator = jsonObject.keys()
    while (iterator.hasNext()) {
        val key = iterator.next()
        when (val value = jsonObject.opt(key)) {
            is JSONObject -> {
                map.putMap(key, convert(value))
            }
            is JSONArray -> {
                map.putArray(key, convert(value))
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
                map.putString(key, value.toString())
            }
        }
    }
    return map
}

fun convert(jsonArray: JSONArray): WritableArray {
    val array: WritableArray = WritableNativeArray()
    for (i in 0 until jsonArray.length()) {
        when (val value = jsonArray.opt(i)) {
            is JSONObject -> {
                array.pushMap(convert(value))
            }
            is JSONArray -> {
                array.pushArray(convert(value))
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
                array.pushString(value.toString())
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
