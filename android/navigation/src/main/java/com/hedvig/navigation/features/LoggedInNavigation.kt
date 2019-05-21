package com.hedvig.navigation.features

import android.content.Context
import android.content.Intent

object LoggedInNavigation {
    private const val LOGGED_IN_ACTIVITY = "com.hedvig.logged_in.LoggedInActivity"

    fun getIntent(context: Context) =
        Intent(context, Class.forName(LOGGED_IN_ACTIVITY))
}
