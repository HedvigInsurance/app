package com.hedvig.app

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import com.hedvig.app.feature.push.PushNotificator
import com.hedvig.app.util.extensions.compatFont
import kotlinx.android.synthetic.main.app_bar.*
import org.koin.android.ext.android.inject

class DevActivity : AppCompatActivity() {

    private val pushNotificator: PushNotificator by inject()

    private val soRay by lazy { compatFont(R.font.soray_extrabold) }
    private val referralPushNotification by lazy { findViewById<Button>(R.id.referralPushNotification) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dev)

        setSupportActionBar(toolbar)
        collapsingToolbar.setExpandedTitleTypeface(soRay)
        collapsingToolbar.setCollapsedTitleTypeface(soRay)

        referralPushNotification.setOnClickListener {
            pushNotificator.sendReferralCompletedNotification()
        }
    }
}
