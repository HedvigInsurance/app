package com.hedvig.app.feature.loggedin

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.app.MainActivity
import com.hedvig.app.R
import com.hedvig.app.feature.whatsnew.WhatsNewDialog
import com.hedvig.app.feature.whatsnew.WhatsNewViewModel
import com.hedvig.app.util.extensions.observe
import kotlinx.android.synthetic.main.logged_in_screen.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LoggedInFragment : Fragment() {

    private val whatsNewViewModel: WhatsNewViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.logged_in_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabContentContainer.adapter = TabPagerAdapter(childFragmentManager)
        bottomTabs.setOnNavigationItemSelectedListener { menuItem ->
            tabContentContainer.setCurrentItem(LoggedInTabs.fromId(menuItem.itemId).ordinal, false)
            true
        }
      
        whatsNewViewModel.news.observe(this) { data ->
            data?.let {
                if (data.news.size > 0) {
                    WhatsNewDialog().show(childFragmentManager, WhatsNewDialog.TAG)
                }
            }
        }
      
        if (requireActivity().intent.getBooleanExtra(MainActivity.EXTRA_NAVIGATE_TO_PROFILE_ON_START_UP, false)) {
            bottomTabs.selectedItemId = R.id.profile
            requireActivity().intent.removeExtra(MainActivity.EXTRA_NAVIGATE_TO_PROFILE_ON_START_UP)
        }
    }
}

