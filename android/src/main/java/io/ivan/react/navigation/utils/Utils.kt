package io.ivan.react.navigation.utils

import android.content.Context
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost


val Context.reactNativeHost: ReactNativeHost get() = (applicationContext as ReactApplication).reactNativeHost