package com.hedvig.logged_in.loggedin

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.hedvig.logged_in.R
import com.hedvig.logged_in.claims.ui.ClaimsFragment
import com.hedvig.logged_in.dashboard.ui.DashboardFragment
import com.hedvig.logged_in.profile.ui.ProfileFragment
import com.hedvig.common.util.extensions.byOrdinal
import com.hedvig.app.R as appR

class TabPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(page: Int): Fragment = when (byOrdinal<LoggedInTabs>(page)) {
        LoggedInTabs.DASHBOARD -> DashboardFragment()
        LoggedInTabs.CLAIMS -> ClaimsFragment()
        LoggedInTabs.PROFILE -> ProfileFragment()
    }

    override fun getCount() = 3
}

enum class LoggedInTabs {
    DASHBOARD,
    CLAIMS,
    PROFILE;

    companion object {
        fun fromId(@IdRes id: Int) = when (id) {
            appR.id.dashboard -> DASHBOARD
            appR.id.claims -> CLAIMS
            appR.id.profile -> PROFILE
            else -> throw Error("Invalid Menu ID")
        }
    }
}
