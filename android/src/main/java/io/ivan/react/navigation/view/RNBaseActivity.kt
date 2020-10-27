package io.ivan.react.navigation.view

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.facebook.infer.annotation.Assertions
import com.facebook.react.devsupport.DoubleTapReloadRecognizer
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import io.ivan.react.navigation.utils.reactNativeHost


open class RNBaseActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler, PermissionAwareActivity {

    private var mPermissionListener: PermissionListener? = null
    private var mDoubleTapReloadRecognizer: DoubleTapReloadRecognizer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        reactNativeHost.reactInstanceManager.createReactContextInBackground()
        mDoubleTapReloadRecognizer = DoubleTapReloadRecognizer()
    }

    override fun onResume() {
        super.onResume()
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onHostResume(this, this)
        }
    }

    override fun onPause() {
        super.onPause()
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onHostPause(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onHostDestroy(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onActivityResult(this, requestCode, resultCode, data)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return run {
            if (reactNativeHost.hasInstance()
                && reactNativeHost.useDeveloperSupport
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
            ) {
                event.startTracking()
                return@run true
            }
            return@run false
        } || super.onKeyDown(keyCode, event)
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent?): Boolean {
        return shouldShowDevMenuOrReload(keyCode, event) || super.onKeyUp(keyCode, event)
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        return run {
            if (reactNativeHost.hasInstance()
                && reactNativeHost.useDeveloperSupport
                && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
            ) {
                reactNativeHost.reactInstanceManager.showDevOptionsDialog()
                return@run true
            }
            return@run false
        } || super.onKeyLongPress(keyCode, event)
    }

    override fun onBackPressed() {
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun invokeDefaultOnBackPressed() {
    }

    override fun onNewIntent(intent: Intent?) {
        if (reactNativeHost.hasInstance()) {
            reactNativeHost.reactInstanceManager.onNewIntent(intent)
        } else {
            super.onNewIntent(intent)
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestPermissions(permissions: Array<String?>?, requestCode: Int, listener: PermissionListener) {
        mPermissionListener = listener
        requestPermissions(permissions!!, requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        if (mPermissionListener != null
            && mPermissionListener!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
        ) {
            mPermissionListener = null
        }
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
                    .didDoubleTapR(keyCode, currentFocus)
            if (didDoubleTapR) {
                reactNativeHost.reactInstanceManager.devSupportManager.handleReloadJS()
                return true
            }
        }
        return false
    }

}
