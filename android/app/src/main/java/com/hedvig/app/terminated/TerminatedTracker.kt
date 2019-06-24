package com.hedvig.app.terminated

import com.google.firebase.analytics.FirebaseAnalytics

class TerminatedTracker(
    private val firebaseAnalytics: FirebaseAnalytics
) {
    fun openChat() = firebaseAnalytics.logEvent("TERMINATED_ALERT_ACTION_CHAT", null)
}
