package com.hedvig.navigation.features

import android.content.Intent
import com.hedvig.navigation.loadIntentOrNull

object LoggedInNavigation : DynamicFeature<Intent> {
    private const val LOGGED_IN_ACTIVITY = "com.hedvig.logged_in.LoggedInActivity"

    override val dynamicStart: Intent?
        get() = LOGGED_IN_ACTIVITY.loadIntentOrNull()
}
