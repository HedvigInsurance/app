package com.hedvig.landing

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController
import com.hedvig.app.BaseActivity
import com.hedvig.app.service.LoginStatus
import com.hedvig.common.util.NavigationAnalytics
import com.hedvig.common.util.extensions.proxyNavigate
import com.hedvig.navigation.features.LoggedInNavigation
import com.hedvig.navigation.features.OnboardingNavigation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import org.koin.android.ext.android.inject
import timber.log.Timber

@SuppressLint("Registered")
class LandingActivity: BaseActivity() {

    val loggedInService: LoginStatusService by inject()
    val navController by lazy { findNavController(R.id.landingNavigationHost) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.landing_navigation_host)

        injectFeature()

        disposables += loggedInService
            .getLoginStatus()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ navigate(it) }, { Timber.e(it) })
    }

    fun navigate(loginStatus: LoginStatus) {
        when (loginStatus) {
            LoginStatus.LOGGED_IN -> {
                startActivity(LoggedInNavigation.getIntent(this))
            }
            LoginStatus.IN_OFFER -> {
                // TODO: navigate to offer screen
                startActivity(OnboardingNavigation.getIntent(this))
            }
            LoginStatus.ONBOARDING -> navController.proxyNavigate(R.id.action_dummyFragment_to_marketingFragment)
        }

        navController.addOnDestinationChangedListener(
            NavigationAnalytics(
                firebaseAnalytics,
                this
            )
        )
    }
}
