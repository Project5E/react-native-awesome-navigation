package io.ivan.react.navigation.view.model

import androidx.lifecycle.ViewModel
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.model.Tabs

class RootViewModel : ViewModel() {
    var tabBarComponentName: String? = null
    var tabs: Tabs? = null
    val navigationOptionCache: MutableMap<String, ReadableMap?> = mutableMapOf()
}
