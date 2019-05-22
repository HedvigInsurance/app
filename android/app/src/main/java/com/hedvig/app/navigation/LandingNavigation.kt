package com.hedvig.app.navigation

object LandingNavigation {

    private const val LANDING_ACTIVITY = "com.hedvig.landing.LandingActivity"

    fun getIntent() = intentTo(LANDING_ACTIVITY)
}
