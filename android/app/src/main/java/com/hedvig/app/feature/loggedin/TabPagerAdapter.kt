package com.hedvig.app.feature.loggedin

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.hedvig.app.feature.claims.ui.ClaimsFragment
import com.hedvig.app.feature.dashboard.ui.DashboardFragment
import com.hedvig.app.feature.profile.ui.ProfileFragment
import com.hedvig.app.util.extensions.byOrdinal

class TabPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(page: Int): Fragment = when (byOrdinal<LoggedInTabs>(page)) {
        LoggedInTabs.DASHBOARD -> DashboardFragment()
        LoggedInTabs.CLAIMS -> ClaimsFragment()
        LoggedInTabs.PROFILE -> ProfileFragment()
    }

    override fun getCount() = 3
}
