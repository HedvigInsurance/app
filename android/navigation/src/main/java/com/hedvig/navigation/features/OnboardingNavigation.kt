package com.hedvig.navigation.features

import android.content.Intent
import com.hedvig.navigation.loadIntentOrNull

object OnboardingNavigation : DynamicFeature<Intent> {
    private const val ON_BOARDING_ACTIVITY = "com.hedvig.onboarding.OnBoardingActivity"

    override val dynamicStart: Intent?
        get() = ON_BOARDING_ACTIVITY.loadIntentOrNull()
}
