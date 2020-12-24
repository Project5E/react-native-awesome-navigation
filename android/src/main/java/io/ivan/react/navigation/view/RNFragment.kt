package io.ivan.react.navigation.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.react.ReactRootView
import io.ivan.react.navigation.NavigationConstants.Companion.VIEW_DID_APPEAR
import io.ivan.react.navigation.NavigationConstants.Companion.VIEW_DID_DISAPPEAR
import io.ivan.react.navigation.NavigationEmitter.sendNavigationEvent
import io.ivan.react.navigation.NavigationManager.resetCurrentActivity
import io.ivan.react.navigation.R
import io.ivan.react.navigation.utils.*
import io.ivan.react.navigation.view.model.RNViewModel
import io.ivan.react.navigation.view.model.createRNViewModel

open class RNFragment : LifecycleFragment() {

    private lateinit var screenId: String
    private lateinit var mainComponentName: String
    private lateinit var reactRootView: ReactRootView
    private lateinit var toolbar: Toolbar

    private val viewModel: RNViewModel by lazy { createRNViewModel(requireActivity().application) }

    override fun setArguments(args: Bundle?) {
        args?.getString(ARG_COMPONENT_NAME)?.let { mainComponentName = it }
        val launchOptions = args?.getBundle(ARG_LAUNCH_OPTIONS)
        val newOptions = launchOptions ?: Bundle()
        screenId = launchOptions?.getString("screenID") ?: View.generateViewId().toString()
        newOptions.putString("screenID", screenId)
        args?.putBundle(ARG_LAUNCH_OPTIONS, newOptions)
        super.setArguments(args)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
//            onPause()
            sendNavigationEvent(VIEW_DID_DISAPPEAR, screenId)
        } else {
//            onResume()
            sendNavigationEvent(VIEW_DID_APPEAR, screenId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isNotTabBarComponent()) {
            viewModel.screenIdStack.add(screenId)
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
        if (isNotTabBarComponent()) {
            setupToolbar()
        }
    }

    override fun onResume() {
        super.onResume()
        sendNavigationEvent(VIEW_DID_APPEAR, screenId)
    }

    override fun onPause() {
        super.onPause()
        sendNavigationEvent(VIEW_DID_DISAPPEAR, screenId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (view as ViewGroup).removeAllViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        resetCurrentActivity(requireActivity())
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
    }

    private fun isNotTabBarComponent() = mainComponentName != viewModel.tabBarComponentName

}