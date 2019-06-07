package com.hedvig.app.feature.offer

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.hedvig.app.R
import com.hedvig.app.ReactBaseActivity
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.extensions.setDarkNavigationBar
import com.hedvig.app.util.extensions.setLightStatusBarText
import com.hedvig.app.util.extensions.statusBarColor

class OfferActivity: ReactBaseActivity() {
    private var mReactRootView: ReactRootView? = null

    private val reactNativeHost: ReactNativeHost
        get() = (application as ReactApplication).reactNativeHost

    private val reactInstanceManager: ReactInstanceManager
        get() = reactNativeHost.reactInstanceManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val reactRootView = ReactRootView(this)
        mReactRootView = reactRootView
        reactRootView.startReactApplication(reactInstanceManager, "Offer")
        setContentView(reactRootView)

        window.statusBarColor = compatColor(R.color.dark_purple)
        setLightStatusBarText()
        setDarkNavigationBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mReactRootView != null) {
            mReactRootView!!.unmountReactApplication()
            mReactRootView = null
        }
        if (reactInstanceManager.lifecycleState != LifecycleState.RESUMED) {
            reactInstanceManager.onHostDestroy(this)
            reactNativeHost.clear()
        }
    }
}
