package com.hedvig.onboarding

import android.annotation.TargetApi
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.navigation.findNavController
import com.airbnb.android.react.lottie.LottiePackage
import com.apollographql.apollo.ApolloClient
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.facebook.react.modules.core.PermissionAwareActivity
import com.facebook.react.modules.core.PermissionListener
import com.facebook.react.shell.MainReactPackage
import com.hedvig.app.BaseActivity
import com.hedvig.app.BuildConfig
import com.hedvig.app.service.LoginStatus
import com.hedvig.common.util.NavigationAnalytics
import com.hedvig.common.util.extensions.proxyNavigate
import com.hedvig.onboarding.react.ActivityStarterReactPackage
import com.hedvig.onboarding.react.AsyncStorageNative
import com.hedvig.onboarding.react.NativeRoutingPackage
import com.horcrux.svg.SvgPackage
import com.leo_pharma.analytics.AnalyticsPackage
import com.lugg.ReactNativeConfig.ReactNativeConfigPackage
import com.rnfs.RNFSPackage
import com.rnim.rn.audio.ReactNativeAudioPackage
import com.zmxv.RNSound.RNSoundPackage
import io.branch.rnbranch.RNBranchModule
import io.branch.rnbranch.RNBranchPackage
import io.sentry.RNSentryPackage
import org.koin.android.ext.android.inject
import timber.log.Timber

class OnBoardingActivity : BaseActivity(), DefaultHardwareBackBtnHandler, PermissionAwareActivity {

    private val apolloClient: ApolloClient by inject()

    private val navController by lazy { findNavController(R.id.onBoardingNavigationHost) }

    private var permissionListener: PermissionListener? = null

    val asyncStorageNative: AsyncStorageNative by inject()

    val loggedInService: LoginStatusService by inject()

    private val reactInstanceManager: ReactInstanceManager
        get() = reactNativeHost.reactInstanceManager

    //todo inject this with koin
    val reactNativeHost by lazy {
        Timber.i("application-> ${application == null}")
        object : ReactNativeHost(application) {
            override fun getUseDeveloperSupport() = BuildConfig.DEBUG

            override fun getPackages() = listOf(
                ActivityStarterReactPackage(apolloClient, asyncStorageNative),
                MainReactPackage(),
//            ImagePickerPackage(),
                RNFSPackage(),
                SvgPackage(),
                ReactNativeConfigPackage(),
                RNSoundPackage(),
                RNSentryPackage(),
                RNBranchPackage(),
                ReactNativeAudioPackage(),
                AnalyticsPackage(),
                LottiePackage(),
                NativeRoutingPackage(apolloClient)
            )

            override fun getJSMainModuleName() = "index.android"
        }
    }


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
