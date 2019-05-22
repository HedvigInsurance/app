package com.hedvig.app

import android.content.Intent
import android.os.Bundle
import com.hedvig.app.navigation.LandingNavigation
import com.hedvig.app.navigation.LoggedInNavigation
import com.hedvig.app.navigation.OnboardingNavigation
import com.hedvig.app.service.LoginStatus
import com.hedvig.app.service.LoginStatusService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

class SplashActivity : BaseActivity() {
    private val loggedInService: LoginStatusService by inject()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        disposables += loggedInService
            .getLoginStatus()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ navigate(it) }, { Timber.e(it) })
    }

    fun navigate(loginStatus: LoginStatus) {

        when (loginStatus) {
            LoginStatus.LOGGED_IN ->
                startActivity(LoggedInNavigation.getIntent())
            LoginStatus.IN_OFFER -> {
                //TODO: navigate to offer screen
                startActivity(OnboardingNavigation.getIntent())
            }
            LoginStatus.ONBOARDING -> startActivity(LandingNavigation.getIntent())
        }
    }

}
