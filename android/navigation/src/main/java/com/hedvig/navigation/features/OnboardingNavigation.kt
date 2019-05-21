package com.hedvig.navigation.features

import android.content.Context
import android.content.Intent

object OnboardingNavigation{
    private const val ON_BOARDING_ACTIVITY = "com.hedvig.onboarding.OnBoardingActivity"

    fun getIntent(context: Context) =
        Intent(context, Class.forName(ON_BOARDING_ACTIVITY))
}
