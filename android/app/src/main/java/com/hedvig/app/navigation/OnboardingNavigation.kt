package com.hedvig.app.navigation

object OnboardingNavigation{
    private const val ON_BOARDING_ACTIVITY = "com.hedvig.onboarding.OnBoardingActivity"

    fun getIntent() = intentTo(ON_BOARDING_ACTIVITY)
}
