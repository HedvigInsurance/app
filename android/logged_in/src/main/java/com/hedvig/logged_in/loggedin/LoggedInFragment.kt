package com.hedvig.logged_in.loggedin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.logged_in.R
import kotlinx.android.synthetic.main.logged_in_screen.*

class LoggedInFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.logged_in_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabContentContainer.adapter = TabPagerAdapter(childFragmentManager)
        bottomTabs.setOnNavigationItemSelectedListener { menuItem ->
            tabContentContainer.currentItem = LoggedInTabs.fromId(menuItem.itemId).ordinal
            true
        }
    }
}

