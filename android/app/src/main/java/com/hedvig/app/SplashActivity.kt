package com.hedvig.app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.hedvig.app.feature.marketing.ui.MarketingActivity
import com.hedvig.app.feature.offer.OfferActivity
import com.hedvig.app.feature.referrals.ReferralsReceiverActivity
import com.hedvig.app.service.LoginStatus
import com.hedvig.app.service.LoginStatusService
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.safeLet
import com.hedvig.app.util.whenApiVersion
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

class SplashActivity : BaseActivity() {

    val loggedInService: LoginStatusService by inject()

    private var referralCode: String? = null
    private var referralIncentive: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whenApiVersion(Build.VERSION_CODES.M) {
            window.statusBarColor = compatColor(R.color.off_white)
        }
    }

    override fun onStart() {
        super.onStart()

        FirebaseDynamicLinks.getInstance().getDynamicLink(intent).addOnSuccessListener { pendingDynamicLinkData ->
            if (pendingDynamicLinkData != null && pendingDynamicLinkData.link != null) {
                val link = pendingDynamicLinkData.link
                val referee = link.getQueryParameter("memberId")
                val incentive = link.getQueryParameter("incentive")
                if (referee != null && incentive != null) {
                    getSharedPreferences("referrals", Context.MODE_PRIVATE).edit().putString("referee", referee)
                        .putString("incentive", incentive).apply()

                    val b = Bundle()
                    b.putString("invitedByMemberId", referee)
                    b.putString("incentive", incentive)

                    FirebaseAnalytics.getInstance(this).logEvent("referrals_open", b)
                }

                referralCode = link.getQueryParameter("referralCode")
                referralIncentive = link.getQueryParameter("referralIncentive")
            }
        }

        disposables += loggedInService
            .getLoginStatus()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ navigateToActivity(it) }, { Timber.e(it) })
    }

    private fun navigateToActivity(loginStatus: LoginStatus) = when (loginStatus) {
        LoginStatus.ONBOARDING -> {
            safeLet(referralCode, referralIncentive) { referralCode, incentive ->
                val intent = Intent(this, ReferralsReceiverActivity::class.java)
                intent.putExtra(ReferralsReceiverActivity.EXTRA_REFERRAL_CODE, referralCode)
                intent.putExtra(ReferralsReceiverActivity.EXTRA_REFERRAL_INCENTIVE, incentive)
                startActivity(intent)
            } ?: startActivity(Intent(this, MarketingActivity::class.java))
        }
        LoginStatus.IN_OFFER -> {
            val intent = Intent(this, OfferActivity::class.java)
            referralCode?.let { intent.putExtra(OfferActivity.EXTRA_REFERRAL_CODE, it) }
            startActivity(intent)
        }
        LoginStatus.LOGGED_IN -> startActivity(Intent(this, LoggedInActivity::class.java))
        LoginStatus.LOGGED_IN_TERMINATED -> startActivity(In)
    }
}
