package com.hedvig.app.feature.referrals

import android.content.Intent
import com.google.gson.Gson
import com.hedvig.android.owldroid.graphql.RedeemReferralCodeMutation
import com.hedvig.app.react.ActivityStarterModule
import com.hedvig.app.util.extensions.localBroadcastManager

class BroadcastingRedeemCodeDialog : RedeemCodeDialog() {
    override fun onRedeemSuccess(data: RedeemReferralCodeMutation.Data) {
        localBroadcastManager.sendBroadcast(Intent(ActivityStarterModule.REDEEMED_CODE_BROADCAST).apply {
            putExtra(
                ActivityStarterModule.BROADCAST_MESSAGE_NAME,
                ActivityStarterModule.MESSAGE_PROMOTION_CODE_REDEEMED
            )
            putExtra(
                ActivityStarterModule.MESSAGE_PROMOTION_CODE_REDEEMED_DATA,
                    Gson().toJson(data.redeemCode.cost)
                )
        })
        dismiss()
    }

    companion object {
        const val TAG = "broadcastingRedeemCodeDialog"

        fun newInstance() = BroadcastingRedeemCodeDialog()
    }
}
