package com.hedvig.app

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.hedvig.app.feature.marketing.ui.MarketingActivity
import com.hedvig.app.feature.offer.OfferActivity
import com.hedvig.app.service.LoginStatus
import com.hedvig.app.service.LoginStatusService
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.whenApiVersion
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

class SplashActivity : BaseActivity() {

    val loggedInService: LoginStatusService by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        whenApiVersion(Build.VERSION_CODES.M) {
            window.statusBarColor = compatColor(R.color.off_white)
        }

        disposables += loggedInService
            .getLoginStatus()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ navigateToActivity(it) }, { Timber.e(it) })
    }

    private fun navigateToActivity(loginStatus: LoginStatus) = when (loginStatus) {
        LoginStatus.ONBOARDING -> startActivity(Intent(this, MarketingActivity::class.java))
        LoginStatus.IN_OFFER -> startActivity(Intent(this, OfferActivity::class.java))
        LoginStatus.LOGGED_IN -> startActivity(Intent(this, LoggedInActivity::class.java))
    }
}
