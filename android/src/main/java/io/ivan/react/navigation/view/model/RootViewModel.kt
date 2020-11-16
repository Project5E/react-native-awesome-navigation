package io.ivan.react.navigation.view.model

import androidx.lifecycle.ViewModel
import io.ivan.react.navigation.model.Tabs

class RootViewModel : ViewModel() {
    var tabBarComponentName: String? = null
    var tabs: Tabs? = null
}
