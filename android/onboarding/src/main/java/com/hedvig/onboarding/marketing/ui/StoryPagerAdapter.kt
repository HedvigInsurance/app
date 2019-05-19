package com.hedvig.onboarding.marketing.ui

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class StoryPagerAdapter(fragmentManager: FragmentManager, val size: Int) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return StoryFragment.newInstance(position)
    }

    override fun getCount(): Int {
        return size
    }
}
