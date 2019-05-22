package com.hedvig.app.navigation

object LoggedInNavigation {
    private const val LOGGED_IN_ACTIVITY = "com.hedvig.logged_in.LoggedInActivity"

    fun getIntent() = intentTo(LOGGED_IN_ACTIVITY)
}
