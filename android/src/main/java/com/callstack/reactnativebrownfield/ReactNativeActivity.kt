package com.callstack.reactnativebrownfield

import android.annotation.TargetApi
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import com.facebook.infer.annotation.Assertions
import com.facebook.react.ReactActivity
import com.facebook.react.ReactActivityDelegate
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactRootView
import com.facebook.react.devsupport.DoubleTapReloadRecognizer
import com.facebook.react.modules.core.PermissionListener
import com.facebook.react.bridge.Callback
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity

class ReactNativeActivity : ReactActivity(), DefaultHardwareBackBtnHandler, PermissionAwareActivity {
    companion object {
        const val MODULE_NAME = "com.callstack.reactnativebrownfield.MODULE_NAME"
    }

    private var reactRootView: ReactRootView? = null
    private lateinit var moduleName: String
    private lateinit var doubleTapReloadRecognizer: DoubleTapReloadRecognizer
    private lateinit var permissionsCallback: Callback
    private var permissionListener: PermissionListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        moduleName = intent.getStringExtra(MODULE_NAME)

        reactRootView = ReactRootView(this)
        reactRootView?.startReactApplication(
            BridgeManager.shared.reactNativeHost.reactInstanceManager,
            moduleName,
            null
        )

        supportActionBar?.hide()

        setContentView(reactRootView)

        doubleTapReloadRecognizer = DoubleTapReloadRecognizer()
    }

    override fun onDestroy() {
        super.onDestroy()
        reactRootView?.unmountReactApplication()
        reactRootView = null

        if (BridgeManager.shared.reactNativeHost.hasInstance()) {
            BridgeManager.shared.reactNativeHost.reactInstanceManager?.onHostDestroy(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (BridgeManager.shared.reactNativeHost.hasInstance()) {
            BridgeManager.shared.reactNativeHost.reactInstanceManager?.onActivityResult(this, requestCode, resultCode, data)
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (BridgeManager.shared.reactNativeHost.hasInstance()
            && BridgeManager.shared.reactNativeHost.useDeveloperSupport
            && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
        ) {
            event.startTracking()
            return true
        }
        return false
    }

    override fun onKeyUp(keyCode: Int, event: KeyEvent): Boolean {
        if (BridgeManager.shared.reactNativeHost.hasInstance()
            && BridgeManager.shared.reactNativeHost.useDeveloperSupport) {
            if (keyCode == KeyEvent.KEYCODE_MENU) {
                BridgeManager.shared.reactNativeHost.reactInstanceManager.showDevOptionsDialog()
                return true
            }
            val didDoubleTapR = Assertions.assertNotNull(doubleTapReloadRecognizer)
                .didDoubleTapR(keyCode, this.getCurrentFocus())
            if (didDoubleTapR) {
                BridgeManager.shared.reactNativeHost.reactInstanceManager.devSupportManager.handleReloadJS()
                return true
            }
        }
        return false
    }

    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
        if (BridgeManager.shared.reactNativeHost.hasInstance()
            && BridgeManager.shared.reactNativeHost.useDeveloperSupport
            && keyCode == KeyEvent.KEYCODE_MEDIA_FAST_FORWARD
        ) {
            BridgeManager.shared.reactNativeHost.reactInstanceManager.showDevOptionsDialog()
            return true
        }
        return false
    }

    override fun onBackPressed() {
        super.invokeDefaultOnBackPressed()
//        if (BridgeManager.shared.reactNativeHost.hasInstance()) {
//            BridgeManager.shared.reactNativeHost.reactInstanceManager.onBackPressed()
//        }
    }

    override fun invokeDefaultOnBackPressed() {
        super.onBackPressed()
    }

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestPermissions(
        permissions: Array<String>,
        requestCode: Int,
        listener: PermissionListener
    ) {
        permissionListener = listener
        this.requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsCallback = Callback {
            if (permissionListener != null) {
                permissionListener?.onRequestPermissionsResult(
                    requestCode,
                    permissions,
                    grantResults
                )

                permissionListener = null
            }
        }
    }
}
