package com.hedvig.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.hedvig.app.feature.referrals.ReferralsReceiverActivity
import com.hedvig.app.feature.referrals.ReferralsSuccessfulInviteActivity
import com.hedvig.app.feature.whatsnew.WhatsNewDialog
import com.hedvig.app.util.extensions.makeToast
import com.hedvig.app.util.extensions.showAlert
import com.hedvig.app.util.extensions.view.setHapticClickListener

class DevelopmentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_development)

        findViewById<Button>(R.id.openWhatsNew).setHapticClickListener {
            WhatsNewDialog.newInstance("2.8.1").show(supportFragmentManager, "whats_new")
        }

        findViewById<Button>(R.id.openAlert).setHapticClickListener {
            showAlert(
                R.string.alert_title,
                R.string.alert_message,
                positiveAction = {
                    makeToast("Positive action activated")
                },
                negativeAction = {
                    makeToast("Negative action activated")
                }
            )
        }

        findViewById<Button>(R.id.openReferralReceiver).setHapticClickListener {
            startActivity(ReferralsReceiverActivity.newInstance(this, "CODE12", "10.00"))
        }
        findViewById<Button>(R.id.openReferralNotification).setHapticClickListener {
            startActivity(ReferralsSuccessfulInviteActivity.newInstance(this, "Fredrik", "10.00"))
        }
    }
}
