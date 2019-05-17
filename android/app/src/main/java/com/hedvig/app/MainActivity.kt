package com.hedvig.app

import android.annotation.TargetApi
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.hedvig.app.service.LoginStatus
import com.hedvig.app.service.LoginStatusService
import com.hedvig.common.util.NavigationAnalytics
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.common.util.extensions.proxyNavigate
import com.hedvig.common.util.react.AsyncStorageNative
import com.hedvig.common.util.whenApiVersion
import com.ice.restring.Restring
import io.branch.rnbranch.RNBranchModule
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

class MainActivity : AppCompatActivity(), DefaultHardwareBackBtnHandler, PermissionAwareActivity {

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

    val asyncStorageNative: AsyncStorageNative by inject()

    val firebaseAnalytics: FirebaseAnalytics by inject()

    val loggedInService: LoginStatusService by inject()

    private val disposables = CompositeDisposable()

    private val reactInstanceManager: ReactInstanceManager
        get() = reactNativeHost.reactInstanceManager

    private val reactNativeHost: ReactNativeHost
        get() = (application as ReactApplication).reactNativeHost

    private val navController by lazy { findNavController(R.id.rootNavigationHost) }

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(Restring.wrapContext(newBase))
    }

    override fun onStart() {
        super.onStart()
        RNBranchModule.initSession(intent.data, this)

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener { pendingDynamicLinkData ->
            if (pendingDynamicLinkData != null && pendingDynamicLinkData.link != null) {
                val link = pendingDynamicLinkData.link
                val referee = link.getQueryParameter("memberId")
                val incentive = link.getQueryParameter("incentive")
                if (referee != null && incentive != null) {
                    getSharedPreferences("referrals", Context.MODE_PRIVATE).edit().putString("referee", referee)
                        .putString("incentive", incentive).apply()

                    val b = Bundle()
                    b.putString("invitedByMemberId", referee)
                    b.putString("incentive", incentive)

                    FirebaseAnalytics.getInstance(this).logEvent("referrals_open", b)
                }
            }
        }
    }

    public override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    override fun onPause() {
        reactInstanceManager.onHostPause(this)
        super.onPause()
    }

    override fun onResume() {
        reactInstanceManager.onHostResume(this, this)
        super.onResume()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Exponent_Light)
        super.onCreate(savedInstanceState)
        whenApiVersion(Build.VERSION_CODES.M) {
            window.statusBarColor = compatColor(R.color.off_white)
        }

        disposables += loggedInService
            .getLoginStatus()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ setupNavGraph(it) }, { Timber.e(it) })
    }

    fun setupNavGraph(loginStatus: LoginStatus) {
        setContentView(R.layout.root_navigation_host)

        when (loginStatus) {
            LoginStatus.LOGGED_IN -> navController.proxyNavigate(R.id.action_dummyFragment_to_logged_in_navigation)
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
}
