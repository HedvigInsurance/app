package com.hedvig.app.feature.loggedin

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.hedvig.app.R
import com.hedvig.app.feature.claims.ui.ClaimsFragment
import com.hedvig.app.feature.dashboard.ui.DashboardFragment
import com.hedvig.app.feature.profile.ui.ProfileFragment
import com.hedvig.common.util.extensions.byOrdinal

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
            R.id.dashboard -> DASHBOARD
            R.id.claims -> CLAIMS
            R.id.profile -> PROFILE
            else -> throw Error("Invalid Menu ID")
        }
    }
}
