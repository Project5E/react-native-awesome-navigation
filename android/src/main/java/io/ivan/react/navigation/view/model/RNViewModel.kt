package io.ivan.react.navigation.view.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import com.facebook.react.bridge.ReadableMap
import io.ivan.react.navigation.model.Tabs

val rnViewModelStore = ViewModelStore()

fun createRNViewModel(application: Application): RNViewModel =
    ViewModelProvider(
        rnViewModelStore,
        ViewModelProvider.AndroidViewModelFactory(application)
    ).get(RNViewModel::class.java)


class RNViewModel(application: Application) : AndroidViewModel(application) {
    var tabBarComponentName: String = ""
    var tabs: Tabs? = null
    val navigationOptionCache: MutableMap<String, ReadableMap?> = mutableMapOf()
}
