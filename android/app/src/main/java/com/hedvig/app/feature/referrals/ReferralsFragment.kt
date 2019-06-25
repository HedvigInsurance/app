package com.hedvig.app.feature.referrals

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.app.BuildConfig
import com.hedvig.app.R
import com.hedvig.app.feature.loggedin.ui.BaseTabFragment
import com.hedvig.app.feature.profile.service.ProfileTracker
import com.hedvig.app.feature.profile.ui.ProfileViewModel
import com.hedvig.app.feature.profile.ui.referral.InvitesAdapter
import com.hedvig.app.ui.decoration.BottomPaddingItemDecoration
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.setupLargeTitle
import com.hedvig.app.util.extensions.showShareSheet
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.updatePadding
import com.hedvig.app.util.interpolateTextKey
import com.hedvig.app.util.safeLet
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_new_referral.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class ReferralsFragment : BaseTabFragment() {

    val tracker: ProfileTracker by inject()

    val profileViewModel: ProfileViewModel by sharedViewModel()

    override val layout = R.layout.fragment_new_referral

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupLargeTitle(R.string.PROFILE_REFERRAL_TITLE, R.font.circular_bold)
        setHasOptionsMenu(true)

        invites.addItemDecoration(
            BottomPaddingItemDecoration(
                resources.getDimensionPixelSize(R.dimen.referral_extra_bottom_space)
            )
        )

        profileViewModel.data.observe(this) { data ->
            safeLet(
                data?.paymentWithDiscount?.grossPremium?.amount?.toBigDecimal()?.toInt(),
                data?.memberReferralCampaign
            ) { monthlyCost, referralCampaign ->
                bindData(monthlyCost, referralCampaign)
            } ?: Timber.e("No data")

            data?.memberReferralCampaign?.referralInformation?.let { referralInformation ->
                bindReferralsButton(
                    referralInformation.incentive.amount.toBigDecimal().toDouble(),
                    referralInformation.code,
                    BuildConfig.REFERRALS_LANDING_BASE_URL + referralInformation.code
                )
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.referral_more_info_menu, menu)
        toolbar.updatePadding(end = resources.getDimensionPixelSize(R.dimen.base_margin_double))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.referralMoreInfo -> {
                profileViewModel.data.value?.memberReferralCampaign?.referralInformation?.incentive?.amount?.toBigDecimal()
                    ?.toInt()?.toString()?.let { incentive ->
                        ReferralBottomSheet.newInstance(incentive)
                            .show(childFragmentManager, "moreInfoSheet")
                    }
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun bindData(monthlyCost: Int, data: ProfileQuery.MemberReferralCampaign) {
        invites.adapter = InvitesAdapter(monthlyCost, data)
    }

    private fun bindReferralsButton(incentive: Double, code: String, referralLink: String) {
        referralButton.setHapticClickListener {
            tracker.clickReferral(incentive.toInt())
            requireContext().showShareSheet("TODO Copy") { intent ->
                intent.apply {
                    putExtra(
                        Intent.EXTRA_TEXT,
                        interpolateTextKey(
                            resources.getString(R.string.REFERRAL_SMS_MESSAGE),
                            "REFERRAL_VALUE" to incentive.toString(),
                            "REFERRAL_CODE" to code,
                            "REFERRAL_LINK" to referralLink
                        )
                    )
                    type = "text/plain"
                }
            }
        }
    }
}

