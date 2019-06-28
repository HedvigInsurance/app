package com.hedvig.app.feature.referrals

import android.content.Intent
import com.hedvig.app.react.ActivityStarterModule
import com.hedvig.app.util.extensions.localBroadcastManager

class BroadcastingRedeemCodeDialog : RedeemCodeDialog() {
    override fun onRedeemSuccess() {
        localBroadcastManager.sendBroadcast(Intent(ActivityStarterModule.REDEEMED_CODE_BROADCAST).apply {
            putExtra(
                ActivityStarterModule.BROADCAST_MESSAGE_NAME,
                ActivityStarterModule.MESSAGE_PROMOTION_CODE_REDEEMED
            )
        })
        dismiss()
    }

    companion object {
        const val TAG = "broadcastingRedeemCodeDialog"

        fun newInstance() = BroadcastingRedeemCodeDialog()
    }
}
