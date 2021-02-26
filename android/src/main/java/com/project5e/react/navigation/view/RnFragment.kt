package com.project5e.react.navigation.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.react.ReactRootView
import com.project5e.react.navigation.NavigationManager.clearInstanceManagerInHost
import com.project5e.react.navigation.NavigationManager.resetInstanceManagerInHost
import com.project5e.react.navigation.NavigationManager.style
import com.project5e.react.navigation.R
import com.project5e.react.navigation.data.ARG_COMPONENT_NAME
import com.project5e.react.navigation.data.ARG_LAUNCH_OPTIONS
import com.project5e.react.navigation.data.ARG_OPTIONS_SCREEN_ID
import com.project5e.react.navigation.utils.LogFragment
import com.project5e.react.navigation.utils.optBoolean
import com.project5e.react.navigation.utils.optString
import com.project5e.react.navigation.view.model.RnViewModel
import com.project5e.react.navigation.view.model.createRnViewModel

open class RnFragment : LogFragment(), RnComponentLifecycle {

    val screenId get() = _screenId
    val mainComponentName get() = _mainComponentName

    val navController: NavController get() = Navigation.findNavController(requireView())

    private lateinit var _screenId: String
    private lateinit var _mainComponentName: String
    private lateinit var reactRootView: ReactRootView
    private lateinit var toolbar: Toolbar

    private val viewModel: RnViewModel by lazy { createRnViewModel(requireActivity().application) }

    override fun setArguments(args: Bundle?) {
        args?.getString(ARG_COMPONENT_NAME)?.let { _mainComponentName = it }
        val launchOptions = args?.getBundle(ARG_LAUNCH_OPTIONS)
        val newOptions = launchOptions ?: Bundle()
        _screenId = launchOptions?.getString(ARG_OPTIONS_SCREEN_ID) ?: View.generateViewId().toString()
        newOptions.putString(ARG_OPTIONS_SCREEN_ID, _screenId)
        args?.putBundle(ARG_LAUNCH_OPTIONS, newOptions)
        super.setArguments(args)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isNotTabBarComponent()) {
            viewModel.screenIdStack.add(_screenId)
        } else {
            viewModel.tabBarScreenId = _screenId
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parent = inflater.inflate(R.layout.fragment_container_component, container, false) as ViewGroup
        toolbar = parent.findViewById(R.id.toolbar)
        parent.addView(createOrReuseReactRootView(inflater, container, savedInstanceState))
        return parent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isTransparency = arguments?.getBundle(ARG_LAUNCH_OPTIONS)?.getBoolean("isTransparency") ?: false
        if (!isTransparency) {
            view.setBackgroundColor(style.componentContainerBackgroundColor)
        }

        if (isNotTabBarComponent()) {
            setupToolbar()
        }
    }

    override fun onResume() {
        super.onResume()
        sendViewAppearEvent(this, _screenId, true)
    }

    override fun onPause() {
        super.onPause()
        sendViewAppearEvent(this, _screenId, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (view as ViewGroup).removeAllViews()
    }

    override fun onDestroy() {
        clearInstanceManagerInHost()
        super.onDestroy()
        resetInstanceManagerInHost()
//        resetCurrentActivity(requireActivity())
    }

    override fun viewDidAppear() {
        onResume()
    }

    override fun viewDidDisappear() {
        onPause()
    }

    private fun createOrReuseReactRootView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): ReactRootView {
        if (!this::reactRootView.isInitialized) {
            reactRootView = super.onCreateView(inflater, container, savedInstanceState) as ReactRootView
        } else {
            (reactRootView.parent as? ViewGroup?)?.removeView(reactRootView)
        }
        return reactRootView
    }

    private fun setupToolbar() {
        toolbar.visibility = View.VISIBLE
        toolbar.setBackgroundColor(Color.WHITE)

        with(requireActivity() as AppCompatActivity) {
            setSupportActionBar(toolbar)
            toolbar.setupWithNavController(findNavController())

            val navigationOption = viewModel.navigationOptionCache[_mainComponentName]
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
    }

    private fun isNotTabBarComponent() = _mainComponentName != viewModel.tabBarComponentName

}