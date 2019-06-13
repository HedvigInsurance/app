package com.hedvig.app.feature.loggedin

import android.support.annotation.IdRes
import com.hedvig.app.R

enum class LoggedInTabs {
    DASHBOARD,
    CLAIMS,
    PROFILE;

    companion object {
        fun fromId(@IdRes id: Int) = when (id) {
            R.id.dashboard -> DASHBOARD
            R.id.claims -> CLAIMS
            R.id.profile -> PROFILE
            else -> throw Error("Invalid Menu ID")
        }
    }
}
