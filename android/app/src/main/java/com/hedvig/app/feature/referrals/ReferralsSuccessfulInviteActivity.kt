package com.hedvig.app.feature.referrals

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hedvig.app.LoggedInActivity
import com.hedvig.app.R
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.extensions.hideStatusBar
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.interpolateTextKey
import com.hedvig.app.util.whenApiVersion
import kotlinx.android.synthetic.main.referrals_successful_invite_actvity.*
import org.koin.android.ext.android.inject

class ReferralsSuccessfulInviteActivity : AppCompatActivity() {

    private val tracker: ReferralsTracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.referrals_successful_invite_actvity)

        hideStatusBar()
        showSuccess()
        setupButtons()
    }

    private fun showSuccess() {
        referralSuccessImage.show()
        referralSuccessTitle.text = interpolateTextKey(
            getString(R.string.REFERRAL_SUCCESS_HEADLINE),
            "USER" to intent.getStringExtra(EXTRA_REFERRAL_NAME)
        )
        referralSuccessTitle.show()
        referralSuccessBody.text = interpolateTextKey(
            getString(R.string.REFERRAL_SUCCESS_BODY),
            "REFERRAL_VALUE" to intent.getStringExtra(EXTRA_REFERRAL_INCENTIVE)
        )
        referralSuccessBody.show()
    }

    private fun showUltimateSuccess() {
        referralSuccessRoot.setBackgroundColor(compatColor(R.color.yellow))
        referralUltimateSuccessImage.show()
        referralUltimateSuccessTitle.text = getString(R.string.REFERRAL_ULTIMATE_SUCCESS_TITLE)
        referralUltimateSuccessTitle.show()
        referralUltimateSuccessBody.text = getString(R.string.REFERRAL_ULTIMATE_SUCCESS_BODY)
        referralUltimateSuccessBody.show()
    }

    private fun setupButtons() {
        referralSuccessInvite.setHapticClickListener {
            tracker.inviteMoreFriends()
            val intent = Intent(this, LoggedInActivity::class.java)
            intent.putExtra(LoggedInActivity.EXTRA_IS_FROM_REFERRALS_NOTIFICATION, true)
            startActivity(intent)
        }
        referralSuccessCloseButton.setHapticClickListener {
            tracker.closeReferralSuccess()
            finish()
        }
    }

    companion object {
        const val EXTRA_REFERRAL_NAME = "extra_referral_name"
        const val EXTRA_REFERRAL_INCENTIVE = "extra_referral_incentive"

        fun newInstance(context: Context, name: String, incentive: String) = newInstance(context).apply {
            putExtra(EXTRA_REFERRAL_NAME, name)
            putExtra(EXTRA_REFERRAL_INCENTIVE, incentive)
        }
        fun newInstance(context: Context) = Intent(context, ReferralsSuccessfulInviteActivity::class.java)
    }
}
