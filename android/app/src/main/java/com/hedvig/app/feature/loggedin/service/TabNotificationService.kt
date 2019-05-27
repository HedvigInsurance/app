package com.hedvig.app.feature.loggedin.service

import android.content.Context
import com.hedvig.app.feature.loggedin.ui.LoggedInTabs

class TabNotificationService(
    private val context: Context
) {
    fun getTabsWithNotification(): List<LoggedInTabs> {
        context
            .getSharedPreferences(TAB_NOTIFICATION_SHARED_PREFERENCES, Context.MODE_PRIVATE)
            .getString(TAB_NOTIFICATION_SHARED_PREFERENCES_KEY, null)
    }

    companion object {
        private const val TAB_NOTIFICATION_SHARED_PREFERENCES = "tab_notifications"
        private const val TAB_NOTIFICATION_SHARED_PREFERENCES_KEY = "notifications"
    }
}
