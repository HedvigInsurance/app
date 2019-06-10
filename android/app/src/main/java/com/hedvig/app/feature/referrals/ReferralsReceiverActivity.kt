package com.hedvig.app.feature.referrals

import android.content.Intent
import android.os.Bundle
import com.hedvig.app.BaseActivity
import com.hedvig.app.R
import com.hedvig.app.feature.chat.ChatActivity
import com.hedvig.app.util.extensions.removeReferralsCode
import kotlinx.android.synthetic.main.referrals_receiver_activity.*

class ReferralsReceiverActivity: BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.referrals_receiver_activity)

        referralReceiverContinueButton.setOnClickListener {
            startChat()
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
