package com.hedvig.app.feature.chat

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.facebook.react.ReactApplication
import com.facebook.react.ReactInstanceManager
import com.facebook.react.ReactNativeHost
import com.facebook.react.ReactRootView
import com.facebook.react.common.LifecycleState
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler
import com.hedvig.app.R
import com.hedvig.app.feature.marketing.ui.MarketingFragment
import com.hedvig.app.react.ActivityStarterModule.Companion.BROADCAST_RELOAD_CHAT
import com.hedvig.app.react.NativeRoutingModule.Companion.NAVIGATE_ROUTING_EXTRA_NAME_ACTION
import com.hedvig.app.react.NativeRoutingModule.Companion.NAVIGATE_ROUTING_EXTRA_VALUE_RESTART_CHAT_ON_BOARDING
import com.hedvig.app.react.NativeRoutingModule.Companion.ON_BOARDING_INTENT_FILER
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.extensions.localBroadcastManager
import com.hedvig.app.util.extensions.statusBarColor
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.newBroadcastReceiver
import com.hedvig.app.util.showRestartDialog
import kotlinx.android.synthetic.main.fragment_chat.*
import kotlinx.android.synthetic.main.fragment_chat.view.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class ChatFragment : Fragment(), DefaultHardwareBackBtnHandler {
    val chatViewModel: ChatViewModel by sharedViewModel()

    private var reactRootView: ReactRootView? = null

    private val reactNativeHost: ReactNativeHost
        get() = (requireActivity().application as ReactApplication).reactNativeHost

    private val reactInstanceManager: ReactInstanceManager
        get() = reactNativeHost.reactInstanceManager

    private var broadcastReceiver: BroadcastReceiver? = null

    private val navController by lazy { requireActivity().findNavController(R.id.rootNavigationHost) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        val reactRootView = ReactRootView(requireContext())
        this.reactRootView = reactRootView
        view.reactViewContainer.addView(this.reactRootView)
        val reactArgs = Bundle().also {
            it.putString(
                ARGS_INTENT,
                arguments?.getString(ARGS_INTENT) ?: MarketingFragment.MarketingResult.ONBOARD.toString()
            )
        }
        reactRootView.startReactApplication(reactInstanceManager, "Chat", reactArgs)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        statusBarColor = requireContext().compatColor(R.color.off_white)

        arguments?.getBoolean(ARGS_SHOW_RESTART)?.let { showRestart ->
            if (showRestart) {
                resetChatButton.show()
            }
        }
        arguments?.getBoolean(ARGS_SHOW_CLOSE)?.let { showClose ->
            if (showClose) {
                closeChatButton.show()
            }
        }

        resetChatButton.setOnClickListener {
            requireContext().showRestartDialog {
                loadingSpinner.show()
                chatViewModel.logout { broadcastLogout() }
            }
        }

        closeChatButton.setOnClickListener {
            navController.popBackStack()
        }
    }

    private fun broadcastLogout() {
        broadcastReceiver = newBroadcastReceiver { _, _ ->
            loadingSpinner.remove()
        }.also { localBroadcastManager.registerReceiver(it, IntentFilter(BROADCAST_RELOAD_CHAT)) }

        localBroadcastManager.sendBroadcast(Intent(ON_BOARDING_INTENT_FILER).also {
            it.putExtra(NAVIGATE_ROUTING_EXTRA_NAME_ACTION, NAVIGATE_ROUTING_EXTRA_VALUE_RESTART_CHAT_ON_BOARDING)
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        if (reactRootView != null) {
            reactRootView!!.unmountReactApplication()
            reactRootView = null
        }
        if (reactInstanceManager.lifecycleState != LifecycleState.RESUMED) {
            reactInstanceManager.onHostDestroy(activity)
            reactNativeHost.clear()
        }
    }

    override fun invokeDefaultOnBackPressed() {
        activity?.onBackPressed()
    }

    companion object {
        const val ARGS_INTENT = "intent"
        const val ARGS_SHOW_RESTART = "show_restart"
        const val ARGS_SHOW_CLOSE = "show_close"
    }
}
