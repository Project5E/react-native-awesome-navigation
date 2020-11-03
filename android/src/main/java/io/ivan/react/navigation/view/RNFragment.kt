package io.ivan.react.navigation.view

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.facebook.infer.annotation.Assertions
import com.facebook.react.ReactApplication
import com.facebook.react.ReactRootView
import com.facebook.react.devsupport.DoubleTapReloadRecognizer
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import io.ivan.react.navigation.bridge.NavigationConstants
import io.ivan.react.navigation.utils.sendNavigationEvent

const val ARG_COMPONENT_NAME = "arg_component_name"
const val ARG_LAUNCH_OPTIONS = "arg_launch_options"

open class RNFragment : Fragment(), PermissionAwareActivity {

    private var mPermissionListener: PermissionListener? = null
    private var mDoubleTapReloadRecognizer: DoubleTapReloadRecognizer? = null

    private lateinit var reactRootView: ReactRootView

    open var mainComponentName: String? = null
    open var launchOptions: Bundle? = null

    private val reactNativeHost
        get() = (requireActivity().application as ReactApplication).reactNativeHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mainComponentName = it.getString(ARG_COMPONENT_NAME)
            launchOptions = it.getBundle(ARG_LAUNCH_OPTIONS)
        }
        loadApp()
        mDoubleTapReloadRecognizer = DoubleTapReloadRecognizer()
    }

    private fun loadApp() {
        checkNotNull(mainComponentName) { "Cannot loadApp if component name is null" }
        reactRootView = ReactRootView(context)
        reactRootView.startReactApplication(reactNativeHost.reactInstanceManager, mainComponentName, launchOptions)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return reactRootView
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
            findNavController().currentBackStackEntry?.destination?.id?.toString()
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        reactRootView.unmountReactApplication()
//        if (reactNativeHost.hasInstance()) {
//            reactNativeHost.reactInstanceManager.onHostDestroy(requireActivity())
//        }
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

}