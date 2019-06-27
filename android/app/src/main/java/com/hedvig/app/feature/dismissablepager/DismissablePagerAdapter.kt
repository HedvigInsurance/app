package com.hedvig.app.feature.dismissablepager

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class DismissablePagerAdapter(
    fragmentManager: FragmentManager,
    private val data: List<DismissablePagerPage>
) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int) = if (position < data.size) {
        data[position].let { page ->
            DismissablePageFragment.newInstance(page.imageUrl, page.title, page.paragraph)
        }
    } else {
        Fragment()
    }

    override fun getCount() = data.size + 1
}
