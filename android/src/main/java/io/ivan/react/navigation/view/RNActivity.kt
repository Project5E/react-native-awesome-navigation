package io.ivan.react.navigation.view

import android.content.res.Resources
import android.graphics.Color
import android.os.Bundle
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import com.facebook.react.ReactApplication
import com.facebook.react.ReactRootView
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
        intent.extras?.apply {
            getString(ARG_COMPONENT_NAME)?.let { mainComponentName = it }
            getBundle(ARG_LAUNCH_OPTIONS)?.let { launchOptions = it }
        }
        launchOptions.getBundle("screenID") ?: launchOptions.putString("screenID", View.generateViewId().toString())

        reactRootView = createReactRootView()

        setContentView(
            if (mainComponentName == viewModel.tabBarComponentName) {
                reactRootView
            } else {
                LinearLayout(this).also {
                    it.orientation = LinearLayout.VERTICAL
                    it.addView(createToolbar())
                    it.addView(reactRootView)
                }
            }
        )

        setupToolbar()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        navigationUp()
    }

    private fun navigationUp() {
        finish()
    }

    private fun getActionBarHeight(): Int =
        TypedValue().let {
            return if (theme.resolveAttribute(android.R.attr.actionBarSize, it, true)) {
                TypedValue.complexToDimensionPixelSize(it.data, Resources.getSystem().displayMetrics)
            } else 0
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

    private fun createToolbar() =
        Toolbar(this).apply {
            toolbar = this
            layoutParams = Toolbar.LayoutParams(Toolbar.LayoutParams.MATCH_PARENT, getActionBarHeight())
            setBackgroundColor(Color.WHITE)
        }

    private fun createReactRootView(): ReactRootView {
        check(!TextUtils.isEmpty(mainComponentName)) { "Cannot loadApp if component name is null" }
        return ReactRootView(this).apply {
            startReactApplication(reactNativeHost.reactInstanceManager, mainComponentName, launchOptions)
        }
    }
}