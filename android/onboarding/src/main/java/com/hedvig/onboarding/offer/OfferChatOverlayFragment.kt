package com.hedvig.onboarding.offer

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.hedvig.onboarding.react.NativeRoutingModule
import com.hedvig.onboarding.react.NativeRoutingModule.Companion.NAVIGATE_ROUTING_EXTRA_NAME_ACTION
import com.hedvig.onboarding.react.NativeRoutingModule.Companion.NAVIGATE_ROUTING_EXTRA_VALUE_LOGOUT_AND_RESTART_APPLICATION
import com.hedvig.onboarding.R
import com.hedvig.app.util.localBroadcastManager
import com.hedvig.onboarding.OnBoardingActivity
import com.hedvig.onboarding.react.AsyncStorageNative
import com.hedvig.onboarding.chat.ChatViewModel
import com.hedvig.onboarding.util.showRestartDialog
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import com.hedvig.app.R as appR

class OfferChatOverlayFragment : DialogFragment(), DefaultHardwareBackBtnHandler {
    val asyncStorageNative: AsyncStorageNative by inject()

    val chatViewModel: ChatViewModel by sharedViewModel()
    private lateinit var dialogView: ViewGroup

    private var mReactRootView: ReactRootView? = null

    private val reactNativeHost: ReactNativeHost
        get() = (requireActivity() as OnBoardingActivity).reactNativeHost

    private val reactInstanceManager: ReactInstanceManager
        get() = reactNativeHost.reactInstanceManager

    override fun getTheme(): Int {
        return appR.style.DialogTheme
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialogView = LayoutInflater.from(activity).inflate(R.layout.offer_chat_overlay_dialog, null) as ViewGroup
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(dialogView)
        return dialog
    }

    override fun onStart() {
        super.onStart()

        dialog?.window?.apply {
            setGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }
    }

    override fun setupDialog(dialog: Dialog, style: Int) {
        setUpDialogTopBar(dialogView)

        mReactRootView = ReactRootView(requireContext())
        mReactRootView!!.startReactApplication(reactInstanceManager, "OfferChat", arguments)
        (dialogView.findViewById<View>(R.id.reactViewContainer) as FrameLayout).addView(mReactRootView)
    }

    private fun setUpDialogTopBar(dialogView: ViewGroup) {
        val closeDialogButton = dialogView.findViewById<ImageView>(R.id.closeChatDialog)
        closeDialogButton.setOnClickListener { dismiss() }

        val resetButton = dialogView.findViewById<ImageView>(R.id.resetChatButton)
        resetButton.setOnClickListener {
            requireContext().showRestartDialog {
                asyncStorageNative.deleteKey("@hedvig:isViewingOffer")
                chatViewModel.logout { broadcastLogout() }
            }
        }
    }

    private fun broadcastLogout() =
        localBroadcastManager.sendBroadcast(Intent(NativeRoutingModule.ON_BOARDING_INTENT_FILER).also {
            it.putExtra(NAVIGATE_ROUTING_EXTRA_NAME_ACTION, NAVIGATE_ROUTING_EXTRA_VALUE_LOGOUT_AND_RESTART_APPLICATION)
        })

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
