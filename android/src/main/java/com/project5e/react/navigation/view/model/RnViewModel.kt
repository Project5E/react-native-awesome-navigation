package com.project5e.react.navigation.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.navigation.NavGraph
import com.facebook.react.bridge.ReadableMap
import com.project5e.react.navigation.data.react.Root

val rnViewModelStore = ViewModelStore()

fun createRnViewModel(app: Application) =
    ViewModelProvider(rnViewModelStore, ViewModelProvider.AndroidViewModelFactory(app)).get(RnViewModel::class.java)


class RnViewModel(application: Application) : AndroidViewModel(application) {
    var root: MutableLiveData<Root> = MutableLiveData()
    var tabBarScreenId: String? = null
    var currentTabIndex = 0
    val navigationOptionCache: MutableMap<String, ReadableMap?> = mutableMapOf()
    var screenIdStack = mutableListOf<String>()
    var pageResult: ReadableMap? = null
    var cacheNavGraph: NavGraph? = null
}
