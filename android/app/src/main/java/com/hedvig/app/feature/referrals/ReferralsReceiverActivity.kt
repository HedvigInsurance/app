package com.hedvig.app.feature.referrals

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.hedvig.android.owldroid.type.RedeemCodeStatus
import com.hedvig.app.BaseActivity
import com.hedvig.app.R
import com.hedvig.app.feature.chat.ChatActivity
import com.hedvig.app.util.extensions.getReferralsCode
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.removeReferralsCode
import com.hedvig.app.util.interpolateTextKey
import kotlinx.android.synthetic.main.referrals_receiver_activity.*
import org.koin.android.viewmodel.ext.android.viewModel

class ReferralsReceiverActivity : BaseActivity() {

    private val referralViewModel: ReferralViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.referrals_receiver_activity)

        //todo fetch data for this
        referralsReceiverTitle.text = interpolateTextKey(
            getString(R.string.REFERRAL_STARTSCREEN_HEADLINE),
            "USER" to "Fredrik",
            "REFERRAL_VALUE" to "10")

        referralViewModel.redeemCodeStatus.observe(this) { redeemStatusCode ->
            when (redeemStatusCode) {
                RedeemCodeStatus.ACCEPTED -> startChat()
                else -> {
                    //todo handle can't redeem code
                    Toast.makeText(this, "The code ${getReferralsCode()} is invalid!", Toast.LENGTH_LONG).show()
                }
            }
        }
        referralReceiverContinueButton.setOnClickListener {
            referralViewModel.redeemReferralCode(getReferralsCode())
        }
        referralReceiverContinueWithoutButton.setOnClickListener {
            removeReferralsCode()
            startChat()
        }
    }

    private fun startChat() {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("intent", "login")
        intent.putExtra("show_restart", true)
        startActivity(intent)
    }

    companion object {
        const val EXTRA_REFERRAL_CODE = "extra_referral_code"
    }
}
