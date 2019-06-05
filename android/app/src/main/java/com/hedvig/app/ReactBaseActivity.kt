package com.hedvig.app

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import com.hedvig.app.util.react.AsyncStorageNative
import io.branch.rnbranch.RNBranchModule
import org.koin.android.ext.android.inject

abstract class ReactBaseActivity : BaseActivity(), DefaultHardwareBackBtnHandler, PermissionAwareActivity {


    val asyncStorageNative: AsyncStorageNative by inject()

    private val reactInstanceManager: ReactInstanceManager
        get() = reactNativeHost.reactInstanceManager

    private val reactNativeHost: ReactNativeHost
        get() = (application as ReactApplication).reactNativeHost

    private var permissionListener: PermissionListener? = null

    @TargetApi(Build.VERSION_CODES.M)
    override fun requestPermissions(permissions: Array<String>, requestCode: Int, listener: PermissionListener?) {
        permissionListener = listener
        super.requestPermissions(permissions, requestCode)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (permissionListener?.onRequestPermissionsResult(requestCode, permissions, grantResults) == true) {
            permissionListener = null
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        reactInstanceManager.onActivityResult(this, requestCode, resultCode, data)
    }


    override fun onStart() {
        super.onStart()
        RNBranchModule.initSession(intent.data, this)
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onBackPressed() {
        if (reactNativeHost.hasInstance()) {
            reactInstanceManager.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPause() {
        reactInstanceManager.onHostPause(this)
        super.onPause()
    }

    override fun onResume() {
        reactInstanceManager.onHostResume(this, this)
        super.onResume()
    }

    override fun invokeDefaultOnBackPressed() {
        super.onBackPressed()
    }


    override fun onDestroy() {
        asyncStorageNative.close()
        super.onDestroy()
    }

}
