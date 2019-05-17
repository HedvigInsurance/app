package com.hedvig.app.feature.offer

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.hedvig.app.R
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.common.util.extensions.setDarkNavigationBar
import com.hedvig.common.util.extensions.setLightStatusBarText
import com.hedvig.app.util.statusBarColor

class OfferFragment : Fragment(), DefaultHardwareBackBtnHandler {

    private var mReactRootView: ReactRootView? = null

    private val reactNativeHost: ReactNativeHost
        get() = (requireActivity().application as ReactApplication).reactNativeHost

    private val reactInstanceManager: ReactInstanceManager
        get() = reactNativeHost.reactInstanceManager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val reactRootView = ReactRootView(requireContext())
        mReactRootView = reactRootView
        reactRootView.startReactApplication(reactInstanceManager, "Offer", arguments)
        return reactRootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        statusBarColor = requireContext().compatColor(R.color.dark_purple)
        requireActivity().setLightStatusBarText()
        requireActivity().setDarkNavigationBar()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mReactRootView != null) {
            mReactRootView!!.unmountReactApplication()
            mReactRootView = null
        }
        if (reactInstanceManager.lifecycleState != LifecycleState.RESUMED) {
            reactInstanceManager.onHostDestroy(activity)
            reactNativeHost.clear()
        }
    }

    override fun invokeDefaultOnBackPressed() {
        activity?.onBackPressed()
    }
}
