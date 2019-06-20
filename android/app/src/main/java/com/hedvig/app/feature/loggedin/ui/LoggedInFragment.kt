package com.hedvig.app.feature.loggedin.ui

import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.iid.FirebaseInstanceId
import com.hedvig.app.LoggedInActivity
import com.hedvig.app.R
import com.hedvig.app.feature.whatsnew.WhatsNewDialog
import com.hedvig.app.feature.whatsnew.WhatsNewViewModel
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.view.remove
import kotlinx.android.synthetic.main.logged_in_screen.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LoggedInFragment : Fragment() {

    private val tabViewModel: BaseTabViewModel by sharedViewModel()
    private val whatsNewViewModel: WhatsNewViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.logged_in_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tabContentContainer.adapter = TabPagerAdapter(childFragmentManager)
        bottomTabs.setOnNavigationItemSelectedListener { menuItem ->
            tabContentContainer.setCurrentItem(LoggedInTabs.fromId(menuItem.itemId).ordinal, false)
            true
        }

        if (requireActivity().intent.getBooleanExtra(LoggedInActivity.EXTRA_NAVIGATE_TO_PROFILE_ON_START_UP, false)) {
            bottomTabs.selectedItemId = R.id.profile
            requireActivity().intent.removeExtra(LoggedInActivity.EXTRA_NAVIGATE_TO_PROFILE_ON_START_UP)
        }

        bindData()
    }

    private fun bindData() {
        var badge: View? = null

        tabViewModel.tabNotification.observe(this) { tab ->
            if (tab == null) {
                badge?.findViewById<ImageView>(R.id.notificationIcon)?.remove()
            } else {
                when (tab) {
                    TabNotification.REFERRALS -> {
                        val itemView =
                            (bottomTabs.getChildAt(0) as BottomNavigationMenuView).getChildAt(LoggedInTabs.PROFILE.ordinal) as BottomNavigationItemView

                        badge = LayoutInflater
                            .from(requireContext())
                            .inflate(R.layout.bottom_navigation_notification, itemView, true)
                    }
                }
            }
        }

        whatsNewViewModel.news.observe(this) { data ->
            data?.let {
                if (data.news.size > 0) {
                    // Yep, this is actually happening
                    FirebaseInstanceId.getInstance().deleteInstanceId()
                    WhatsNewDialog.newInstance().show(childFragmentManager, WhatsNewDialog.TAG)
                }
            }
        }
        whatsNewViewModel.fetchNews()
    }
}

