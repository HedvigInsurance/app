package com.hedvig.app.feature.whatsnew

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import com.hedvig.android.owldroid.graphql.WhatsNewQuery

class PagerAdapter(fragmentManager: FragmentManager, private val data: List<WhatsNewQuery.News>) : FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment = if (position < data.size) {
        NewsFragment.newInstance(
            data[position].illustration.svgUrl,
            data[position].title,
            data[position].paragraph
        )
    } else {
        Fragment()
    }

    override fun getCount() = data.size + 1
}
