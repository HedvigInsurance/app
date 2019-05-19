package com.hedvig.onboarding

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.navigation.findNavController
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import com.hedvig.app.BaseActivity
import com.hedvig.app.service.LoginStatus
import com.hedvig.common.util.NavigationAnalytics
import com.hedvig.common.util.extensions.proxyNavigate
import com.hedvig.onboarding.react.AsyncStorageNative
import io.branch.rnbranch.RNBranchModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

class OnBoardingActivity : BaseActivity(), DefaultHardwareBackBtnHandler, PermissionAwareActivity {

    private val navController by lazy { findNavController(R.id.onBoardingNavigationHost) }

    private var permissionListener: PermissionListener? = null

    val asyncStorageNative: AsyncStorageNative by inject()
    val loggedInService: com.hedvig.onboarding.LoginStatusService by inject()

    private val reactInstanceManager: ReactInstanceManager
        get() = reactNativeHost.reactInstanceManager

    private val reactNativeHost: ReactNativeHost
        get() = (application as ReactApplication).reactNativeHost


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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_navigation_host)

        injectFeature()

        Timber.i("OnBoardingActivity onCreate")
        setupNavGraph(LoginStatus.ONBOARDING)
        // TODO: don't do this ^
//        disposables += loggedInService
//            .getLoginStatus()
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ setupNavGraph(it) }, { Timber.e(it) })
    }

    override fun onStart() {
        super.onStart()
        RNBranchModule.initSession(intent.data, this)
    }


    override fun onPause() {
        reactInstanceManager.onHostPause(this)
        super.onPause()
    }

    override fun onResume() {
        reactInstanceManager.onHostResume(this, this)
        super.onResume()
    }

    override fun onBackPressed() {
        if (reactNativeHost.hasInstance()) {
            reactInstanceManager.onBackPressed()
        } else {
            super.onBackPressed()
        }
    }

    override fun invokeDefaultOnBackPressed() {
        super.onBackPressed()
    }

    override fun onDestroy() {
        disposables.clear()
        asyncStorageNative.close()
        super.onDestroy()
    }

    fun setupNavGraph(loginStatus: LoginStatus) {

        when (loginStatus) {
            LoginStatus.LOGGED_IN -> {
            }// TODO navController.proxyNavigate(com.hedvig.app.R.id.action_dummyFragment_to_logged_in_navigation)
            LoginStatus.IN_OFFER -> navController.proxyNavigate(R.id.action_dummyFragment_to_offerFragment)
            LoginStatus.ONBOARDING -> navController.proxyNavigate(R.id.action_dummyFragment_to_marketingFragment)
        }

        navController.addOnDestinationChangedListener(
            NavigationAnalytics(
                firebaseAnalytics,
                this
            )
        )
    }
}
