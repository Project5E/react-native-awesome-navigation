package io.ivan.react.navigation.view

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.facebook.infer.annotation.Assertions
import com.facebook.react.ReactApplication
import com.facebook.react.ReactRootView
import com.facebook.react.devsupport.DoubleTapReloadRecognizer
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import io.ivan.react.navigation.NavigationConstants
import io.ivan.react.navigation.R
import io.ivan.react.navigation.utils.*
import io.ivan.react.navigation.view.model.RNViewModel
import io.ivan.react.navigation.view.model.createRNViewModel

open class RNFragment : Fragment(), PermissionAwareActivity {

    open var mainComponentName: String = ""
    open var launchOptions: Bundle = Bundle()

    private var reactRootView: ReactRootView? = null
    private lateinit var toolbar: Toolbar

    private val viewModel: RNViewModel by lazy { createRNViewModel(requireActivity().application) }

    private var mPermissionListener: PermissionListener? = null
    private val mDoubleTapReloadRecognizer: DoubleTapReloadRecognizer by lazy { DoubleTapReloadRecognizer() }

    private val reactNativeHost
        get() = (requireActivity().application as ReactApplication).reactNativeHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.apply {
            getString(ARG_COMPONENT_NAME)?.let { mainComponentName = it }
            getBundle(ARG_LAUNCH_OPTIONS)?.let { launchOptions = it }
        }
        launchOptions.getBundle("screenID") ?: launchOptions.putString("screenID", View.generateViewId().toString())
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val parent = inflater.inflate(R.layout.component_container, container, false) as ViewGroup
        toolbar = parent.findViewById(R.id.toolbar)
        if (reactRootView == null) {
            reactRootView = ReactRootView(inflater.context)
            loadApp()
        } else {
            (reactRootView?.parent as? ViewGroup)?.removeView(reactRootView)
        }
        parent.addView(reactRootView)
        return parent
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar.takeIf { mainComponentName != viewModel.tabBarComponentName }?.apply {
            visibility = View.VISIBLE
            setBackgroundColor(Color.WHITE)
            setupToolbar()
        }
    }

    override fun onResume() {
        super.onResume()
        if (reactNativeHost.hasInstance()) {
            if (requireActivity() is DefaultHardwareBackBtnHandler) {
                reactNativeHost.reactInstanceManager.onHostResume(
                    requireActivity(),
                    requireActivity() as DefaultHardwareBackBtnHandler?
                )
            } else {
                throw ClassCastException(
                    "Host Activity does not implement DefaultHardwareBackBtnHandler"
                )
            }
        }
        requireActivity().sendNavigationEvent(
            NavigationConstants.VIEW_DID_APPEAR,
            findNavController().currentBackStackEntry?.destination?.id?.toString()
        )
    }

    override fun onPause() {
        super.onPause()
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onHostPause(requireActivity())
        }
        requireActivity().sendNavigationEvent(
            NavigationConstants.VIEW_DID_DISAPPEAR,
            findNavController().previousBackStackEntry?.destination?.id?.toString()
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        (view as ViewGroup).removeAllViews()
    }

    override fun onDestroy() {
        super.onDestroy()
        reactRootView?.unmountReactApplication()
        reactRootView = null
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onHostDestroy(requireActivity())
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onActivityResult(requireActivity(), requestCode, resultCode, data)
        }
    }

    /**
     * Helper to forward hardware back presses to our React Native Host
     *
     * <p>This must be called via a forward from your host Activity
     */
    fun onBackPressed(): Boolean {
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onBackPressed()
            return true
        }
        return false
    }

    /**
     * Helper to forward onKeyUp commands from our host Activity. This allows ReactFragment to handle
     * double tap reloads and dev menus
     *
     * <p>This must be called via a forward from your host Activity
     *
     * @param keyCode keyCode
     * @param event event
     * @return true if we handled onKeyUp
     */
    fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return shouldShowDevMenuOrReload(keyCode, event)
    }

    /**
     * Handles delegating the [Activity.onKeyUp] method to determine whether the
     * application should show the developer menu or should reload the React Application.
     *
     * @return true if we consume the event and either shoed the develop menu or reloaded the
     * application.
     */
    private fun shouldShowDevMenuOrReload(keyCode: Int, event: KeyEvent?): Boolean {
        if (reactNativeHost.hasInstance() && reactNativeHost.useDeveloperSupport) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                reactNativeHost.reactInstanceManager.showDevOptionsDialog()
                return true
            }
            val didDoubleTapR =
                Assertions.assertNotNull<DoubleTapReloadRecognizer>(mDoubleTapReloadRecognizer)
                    .didDoubleTapR(keyCode, requireActivity().currentFocus)
            if (didDoubleTapR) {
                reactNativeHost.reactInstanceManager.devSupportManager.handleReloadJS()
                return true
            }
        }
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (mPermissionListener != null
            && mPermissionListener!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ) {
            mPermissionListener = null
        }
    }

    override fun checkPermission(permission: String, pid: Int, uid: Int): Int {
        return requireActivity().checkPermission(permission, pid, uid)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun checkSelfPermission(permission: String): Int {
        return requireActivity().checkSelfPermission(permission)
    }

    override fun requestPermissions(permissions: Array<out String>, requestCode: Int, listener: PermissionListener) {
        mPermissionListener = listener
        requestPermissions(permissions, requestCode)
    }

    private fun loadApp() {
        check(!TextUtils.isEmpty(mainComponentName)) { "Cannot loadApp if component name is null" }
        reactRootView?.startReactApplication(reactNativeHost.reactInstanceManager, mainComponentName, launchOptions)
    }

    private fun setupToolbar() {
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

}