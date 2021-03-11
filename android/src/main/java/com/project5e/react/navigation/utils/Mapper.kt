package com.project5e.react.navigation.utils

import com.facebook.react.bridge.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.json.JSONArray
import org.json.JSONObject

object Mapper {
    val gson = Gson()

    @JvmStatic
    inline fun <reified T> ReadableMap?.toObj(): T? = convertRnMapToObj(this)

    @JvmStatic
    inline fun <reified T> ReadableArray?.toArray(): Array<T?>? = convertRnArrayToArray(this)

    @JvmStatic
    inline fun <reified T> ReadableArray?.toList(): MutableList<T?>? = convertRnArrayToList(this)

    @JvmStatic
    inline fun <reified T> T?.toRnMap(): WritableMap = convertObjToRnMap(this)

    @JvmStatic
    inline fun <reified T> Array<T?>?.toRnArray(): WritableArray = convertArrayToRnArray(this)

    @JvmStatic
    inline fun <reified T> List<T?>?.toRnArray(): WritableArray = convertListToRnArray(this)

    @JvmStatic
    inline fun <reified T> convertRnMapToObj(map: ReadableMap?): T? {
        map ?: return null

        val json = convertMapToJson(map).toString()
        return gson.fromJson(json, T::class.java)
    }

    @JvmStatic
    inline fun <reified T> convertRnArrayToArray(arr: ReadableArray?): Array<T?>? {
        arr ?: return null

        val json = convertArrayToJson(arr).toString()
        return gson.fromJson(json, object : TypeToken<Array<T>>() {}.type)
    }

    @JvmStatic
    inline fun <reified T> convertRnArrayToList(arr: ReadableArray?): MutableList<T?>? {
        arr ?: return null

        val json = convertArrayToJson(arr).toString()
        return gson.fromJson(json, object : TypeToken<MutableList<T>>() {}.type)
    }

    @JvmStatic
    inline fun <reified T> convertObjToRnMap(t: T?): WritableMap {
        t ?: return Arguments.createMap()

        val json = gson.toJson(t)
        return convertJsonToMap(JSONObject(json))
    }

    @JvmStatic
    inline fun <reified T> convertArrayToRnArray(t: Array<T?>?): WritableArray {
        t ?: return Arguments.createArray()

        val json = gson.toJson(t)
        return convertJsonToArray(JSONArray(json))
    }

    @JvmStatic
    inline fun <reified T> convertListToRnArray(t: List<T?>?): WritableArray {
        t ?: return Arguments.createArray()

        val json = gson.toJson(t)
        return convertJsonToArray(JSONArray(json))
    }

    @JvmStatic
    fun convertMapToJson(map: ReadableMap?): JSONObject {
        val result = JSONObject()
        map ?: return result

        val it = map.keySetIterator()
        while (it.hasNextKey()) {
            val key = it.nextKey()
            when (map.getType(key)) {
                ReadableType.String -> result.put(key, map.getString(key))
                ReadableType.Number -> result.put(key, parseNumber(map, key))
                ReadableType.Boolean -> result.put(key, map.getBoolean(key))
                ReadableType.Array -> result.put(key, convertArrayToJson(map.getArray(key)))
                ReadableType.Map -> result.put(key, convertMapToJson(map.getMap(key)))
                else -> {
                }
            }
        }
        return result
    }

    @JvmStatic
    fun convertArrayToJson(arr: ReadableArray?): JSONArray {
        val result = JSONArray()
        arr ?: return result

        for (i in 0 until arr.size()) {
            when (arr.getType(i)) {
                ReadableType.String -> result.put(arr.getString(i))
                ReadableType.Number -> result.put(parseNumber(arr, i))
                ReadableType.Boolean -> result.put(arr.getBoolean(i))
                ReadableType.Array -> result.put(convertArrayToJson(arr.getArray(i)))
                ReadableType.Map -> result.put(convertMapToJson(arr.getMap(i)))
                else -> {
                }
            }
        }
        return result
    }

    @JvmStatic
    fun convertJsonToMap(jsonObject: JSONObject): WritableMap {
        val map: WritableMap = WritableNativeMap()
        val iterator = jsonObject.keys()
        while (iterator.hasNext()) {
            val key = iterator.next()
            when (val value = jsonObject.opt(key)) {
                is JSONObject -> {
                    map.putMap(key, convertJsonToMap(value))
                }
                is JSONArray -> {
                    map.putArray(key, convertJsonToArray(value))
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

    @JvmStatic
    fun convertJsonToArray(jsonArray: JSONArray): WritableArray {
        val array: WritableArray = WritableNativeArray()
        for (i in 0 until jsonArray.length()) {
            when (val value = jsonArray.opt(i)) {
                is JSONObject -> {
                    array.pushMap(convertJsonToMap(value))
                }
                is JSONArray -> {
                    array.pushArray(convertJsonToArray(value))
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
            if (doubleValue % 1 == 0.0) map.getInt(key) else doubleValue
        } catch (e: Exception) {
            map.getInt(key)
        }
    }

    private fun parseNumber(arr: ReadableArray, index: Int): Any {
        return try {
            val doubleValue = arr.getDouble(index)
            if (doubleValue % 1 == 0.0) arr.getInt(index) else doubleValue
        } catch (e: Exception) {
            arr.getInt(index)
        }
    }

}

