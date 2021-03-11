package com.project5e.react.navigation.data

import android.os.Bundle
import androidx.navigation.NavOptions
import com.facebook.react.bridge.ReadableMap

data class DestinationArgument(
    val name: String? = null,
    val args: Bundle? = null,
    val rnArgs: ReadableMap? = null,
    val navOptions: NavOptions? = null,
)
