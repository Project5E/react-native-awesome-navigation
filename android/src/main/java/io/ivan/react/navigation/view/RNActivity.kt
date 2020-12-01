package io.ivan.react.navigation.view

import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.widget.Toolbar
import com.facebook.react.ReactApplication
import com.facebook.react.ReactRootView
import io.ivan.react.navigation.R
import io.ivan.react.navigation.utils.ARG_COMPONENT_NAME
import io.ivan.react.navigation.utils.ARG_LAUNCH_OPTIONS
import io.ivan.react.navigation.utils.optBoolean
import io.ivan.react.navigation.utils.optString
import io.ivan.react.navigation.view.model.RNViewModel
import io.ivan.react.navigation.view.model.createRNViewModel

open class RNActivity : RNBaseActivity() {

    open var mainComponentName: String = ""
    open var launchOptions: Bundle = Bundle()

    private lateinit var reactRootView: ReactRootView
    private lateinit var toolbar: Toolbar

    private val viewModel: RNViewModel by lazy { createRNViewModel(application) }

    private val reactNativeHost
        get() = (application as ReactApplication).reactNativeHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.component_container)

        toolbar = findViewById(R.id.toolbar)
        reactRootView = ReactRootView(this)

        intent.extras?.apply {
            getString(ARG_COMPONENT_NAME)?.let { mainComponentName = it }
            getBundle(ARG_LAUNCH_OPTIONS)?.let { launchOptions = it }
        }
        launchOptions.getBundle("screenID") ?: launchOptions.putString("screenID", View.generateViewId().toString())

        loadApp()

        toolbar.takeIf { mainComponentName != viewModel.tabBarComponentName }?.apply {
            visibility = View.VISIBLE
            setBackgroundColor(Color.WHITE)
            setupToolbar()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigationUp()
    }

    private fun navigationUp() {
        finish()
    }

    private fun setupToolbar() {
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            navigationUp()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val navigationOption = viewModel.navigationOptionCache[mainComponentName]
        when (navigationOption?.optBoolean("hideNavigationBar")) {
            true -> {
                supportActionBar?.hide()
            }
            else -> {
                supportActionBar?.show()
                supportActionBar?.title = navigationOption?.optString("title") ?: ""
            }
        }
    }

    private fun loadApp() {
        check(!TextUtils.isEmpty(mainComponentName)) { "Cannot loadApp if component name is null" }
        reactRootView.startReactApplication(reactNativeHost.reactInstanceManager, mainComponentName, launchOptions)
    }
}