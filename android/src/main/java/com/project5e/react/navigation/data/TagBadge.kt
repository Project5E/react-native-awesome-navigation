package com.project5e.react.navigation.data

import android.text.TextUtils
import com.facebook.react.bridge.ReadableMap
import com.project5e.react.navigation.utils.optBoolean
import com.project5e.react.navigation.utils.optInt
import com.project5e.react.navigation.utils.optString
import q.rorbin.badgeview.QBadgeView

data class TabBadge(
    val index: Int,
    val hidden: Boolean,
    val text: String?,
    val dot: Boolean?,
) {
    constructor(map: ReadableMap) : this(
        map.optInt("index") ?: 0,
        map.optBoolean("hidden") ?: false,
        map.optString("text"),
        map.optBoolean("dot"),
    )

    fun bind(views: MutableList<QBadgeView>) {
        if (index > views.size - 1) throw IndexOutOfBoundsException("index > tabs.size")
        val badgeView = views[index]
        when {
            hidden -> {
                badgeView.hide(true)
                return
            }
            dot == true -> {
                badgeView.badgeNumber = -1
                return
            }
            text == null -> {
                return
            }
            TextUtils.isDigitsOnly(text) -> {
                badgeView.badgeNumber = text.toInt()
                return
            }
            !TextUtils.isDigitsOnly(text) -> {
                badgeView.badgeText = text
                return
            }
        }
    }

}
