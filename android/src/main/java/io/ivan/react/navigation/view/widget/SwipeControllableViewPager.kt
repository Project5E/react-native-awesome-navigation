package io.ivan.react.navigation.view.widget

import android.content.Context
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class SwipeControllableViewPager(context: Context) : ViewPager(context) {

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return isEnabled && super.onInterceptTouchEvent(event)
    }

}