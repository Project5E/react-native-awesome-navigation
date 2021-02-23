package com.project5e.react.navigation.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.facebook.react.bridge.ReadableMap
import com.project5e.react.navigation.data.Tabs

val rnViewModelStore = ViewModelStore()

fun createRnViewModel(app: Application) =
    ViewModelProvider(rnViewModelStore, ViewModelProvider.AndroidViewModelFactory(app)).get(RnViewModel::class.java)


class RnViewModel(application: Application) : AndroidViewModel(application) {
    var tabBarScreenId: String? = null
    var tabBarComponentName: String? = null
    var tabs: Tabs? = null
    var currentTabIndex = 0
    val navigationOptionCache: MutableMap<String, ReadableMap?> = mutableMapOf()
    var screenIdStack = mutableListOf<String>()
    var pageResult: ReadableMap? = null
}
