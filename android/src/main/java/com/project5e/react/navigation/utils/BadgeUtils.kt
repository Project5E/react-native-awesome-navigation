package com.project5e.react.navigation.utils

import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationMenuView
import com.google.android.material.bottomnavigation.BottomNavigationView
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

/**
 * @param number 显示的数字，等于0不显示，小于0显示小红点
 */
fun showBadge(bottomNavigationView: BottomNavigationView, viewIndex: Int, number: Int) {
    val badge = getTabItemBadge(bottomNavigationView, viewIndex) ?: return
    badge.badgeNumber = number
}

fun showBadge(bottomNavigationView: BottomNavigationView, viewIndex: Int, text: String) {
    val badge = getTabItemBadge(bottomNavigationView, viewIndex) ?: return
    badge.badgeText = text
}

fun getTabItemBadge(bottomNavigationView: BottomNavigationView, viewIndex: Int): Badge? {
    // 具体child的查找和view的嵌套结构请在源码中查看
    // 从bottomNavigationView中获得BottomNavigationMenuView
    val menuView = bottomNavigationView.getChildAt(0) as BottomNavigationMenuView
    // 从BottomNavigationMenuView中获得childView, BottomNavigationItemView
    if (viewIndex >= menuView.childCount) {
        return null
    } else {
        // 获得viewIndex对应子tab
        val view: View = menuView.getChildAt(viewIndex)
        // 从子tab中获得其中显示图片的ImageView
        val icon: View = view.findViewById(com.google.android.material.R.id.icon)
        // 获得图标的宽度
        val iconWidth: Int = icon.width
        // 获得tab的宽度/2
        val tabWidth: Int = view.width / 2
        // 计算badge要距离右边的距离
        val spaceWidth = tabWidth - iconWidth

        return QBadgeView(bottomNavigationView.context)
            .bindTarget(view)
            .setGravityOffset(spaceWidth.toFloat(), 4f, false)
    }
}
