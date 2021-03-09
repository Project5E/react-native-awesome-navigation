package com.project5e.react.navigation.data

import android.text.TextUtils
import com.facebook.react.bridge.ReadableMap
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.project5e.react.navigation.utils.optBoolean
import com.project5e.react.navigation.utils.optInt
import com.project5e.react.navigation.utils.optString
import com.project5e.react.navigation.utils.showBadge

data class TabBadge(
    val index: Int,
    val hidden: Boolean,
    val text: String?,
    val dot: Boolean,
) {
    constructor(map: ReadableMap) : this(
        map.optInt("index") ?: 0,
        map.optBoolean("hidden") ?: false,
        map.optString("text"),
        map.optBoolean("dot") ?: false,
    )

    fun bindTabBar(bottomNavigationView: BottomNavigationView) {
        if (dot) {
            showBadge(bottomNavigationView, index, -1)
            return
        }
        if (TextUtils.isDigitsOnly(text)) {
            showBadge(bottomNavigationView, index, text!!.toInt())
        } else {
            showBadge(bottomNavigationView, index, text!!)
        }
    }

}
