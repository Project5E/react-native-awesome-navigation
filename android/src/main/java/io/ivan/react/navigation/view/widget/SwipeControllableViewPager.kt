package io.ivan.react.navigation.view.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class SwipeControllableViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null) :
    ViewPager(context, attrs) {

    override fun onInterceptTouchEvent(event: MotionEvent?): Boolean {
        return isEnabled && super.onInterceptTouchEvent(event)
    }

}