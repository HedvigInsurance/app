package com.hedvig.app.feature.referrals

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hedvig.app.R
import com.hedvig.app.util.interpolateTextKey
import kotlinx.android.synthetic.main.referrals_successfule_invite_actvity.*

class ReferralsSuccessfulInviteActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.referrals_successfule_invite_actvity)

        referralSuccessTitle.text = interpolateTextKey(
            getString(R.string.REFERRAL_SUCCESS_HEADLINE),
            "USER" to "???" // TODO fetch
        )
        referralSuccessBody.text = interpolateTextKey(
            getString(R.string.REFERRAL_SUCCESS_BODY),
            "REFERRAL_VALUE" to "???" // TODO fetch
        )
    }
}
