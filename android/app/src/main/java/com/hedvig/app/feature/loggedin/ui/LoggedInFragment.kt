package com.hedvig.app.feature.loggedin.ui

import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.design.internal.BottomNavigationMenuView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.google.firebase.iid.FirebaseInstanceId
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.app.LoggedInActivity
import com.hedvig.app.R
import com.hedvig.app.feature.claims.ui.ClaimsViewModel
import com.hedvig.app.feature.profile.ui.ProfileViewModel
import com.hedvig.app.feature.referrals.ReferralBottomSheet
import com.hedvig.app.feature.whatsnew.WhatsNewDialog
import com.hedvig.app.feature.whatsnew.WhatsNewViewModel
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.setupLargeTitle
import com.hedvig.app.util.extensions.startClosableChat
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.updatePadding
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.logged_in_screen.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.viewmodel.ext.android.sharedViewModel
import org.koin.android.viewmodel.ext.android.viewModel

class LoggedInFragment : Fragment() {

    private val claimsViewModel: ClaimsViewModel by sharedViewModel()
    private val tabViewModel: BaseTabViewModel by sharedViewModel()
    private val whatsNewViewModel: WhatsNewViewModel by viewModel()
    private val profileViewModel: ProfileViewModel by sharedViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.logged_in_screen, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        toolbar.updatePadding(end = resources.getDimensionPixelSize(R.dimen.base_margin_double))

        tabContentContainer.adapter = TabPagerAdapter(childFragmentManager)
        bottomTabs.setOnNavigationItemSelectedListener { menuItem ->
            val id = LoggedInTabs.fromId(menuItem.itemId)
            tabContentContainer.setCurrentItem(id.ordinal, false)
            setupAppBar(id)
            true
        }

        if (requireActivity().intent.getBooleanExtra(LoggedInActivity.EXTRA_IS_FROM_REFERRALS_NOTIFICATION, false)) {
            bottomTabs.selectedItemId = R.id.referrals
            requireActivity().intent.removeExtra(LoggedInActivity.EXTRA_IS_FROM_REFERRALS_NOTIFICATION)
        }

        bindData()
        setupAppBar(LoggedInTabs.fromId(bottomTabs.selectedItemId))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        when (LoggedInTabs.fromId(bottomTabs.selectedItemId)) {
            LoggedInTabs.DASHBOARD,
            LoggedInTabs.CLAIMS,
            LoggedInTabs.PROFILE -> {
                inflater.inflate(R.menu.base_tab_menu, menu)
            }
            LoggedInTabs.REFERRALS -> {
                inflater.inflate(R.menu.referral_more_info_menu, menu)
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (LoggedInTabs.fromId(bottomTabs.selectedItemId)) {
            LoggedInTabs.DASHBOARD,
            LoggedInTabs.CLAIMS,
            LoggedInTabs.PROFILE -> {
                claimsViewModel.triggerFreeTextChat {
                    requireActivity().startClosableChat()
                }
            }
            LoggedInTabs.REFERRALS -> {
                (profileViewModel.data.value?.referralInformation?.campaign?.incentive as? ProfileQuery.AsMonthlyCostDeduction)?.amount?.amount?.toBigDecimal()
                    ?.toInt()?.toString()?.let { amount ->
                        ReferralBottomSheet.newInstance(amount).show(childFragmentManager, ReferralBottomSheet.TAG)
                    }
            }
        }
        return true
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
                            (bottomTabs.getChildAt(0) as BottomNavigationMenuView).getChildAt(LoggedInTabs.REFERRALS.ordinal) as BottomNavigationItemView

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
                    GlobalScope.launch(Dispatchers.IO) {
                        FirebaseInstanceId.getInstance().deleteInstanceId()
                    }
                    WhatsNewDialog.newInstance().show(childFragmentManager, WhatsNewDialog.TAG)
                }
            }
        }
        whatsNewViewModel.fetchNews()
    }

    fun setupAppBar(id: LoggedInTabs) {
        activity?.invalidateOptionsMenu()
        appBarLayout.setExpanded(true, false)
        when (id) {
            LoggedInTabs.DASHBOARD -> {
                setupLargeTitle(R.string.DASHBOARD_SCREEN_TITLE, R.font.soray_extrabold)
            }
            LoggedInTabs.CLAIMS -> {
                setupLargeTitle(R.string.CLAIMS_TITLE, R.font.soray_extrabold)
            }
            LoggedInTabs.REFERRALS -> {
                setupLargeTitle(R.string.PROFILE_REFERRAL_TITLE, R.font.soray_extrabold)
            }
            LoggedInTabs.PROFILE -> {
                setupLargeTitle(R.string.PROFILE_TITLE, R.font.soray_extrabold)
            }
        }
    }
}

