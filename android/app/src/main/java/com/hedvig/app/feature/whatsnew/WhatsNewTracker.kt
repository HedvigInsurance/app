package com.hedvig.app.feature.whatsnew

import com.google.firebase.analytics.FirebaseAnalytics

class WhatsNewTracker(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun trackClickProceed() = firebaseAnalytics.logEvent("NEWS_PROCEED", null)
}
