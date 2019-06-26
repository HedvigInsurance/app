package com.hedvig.app.feature.referrals

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.app.BuildConfig
import com.hedvig.app.R
import com.hedvig.app.feature.loggedin.ui.BaseTabFragment
import com.hedvig.app.feature.profile.service.ProfileTracker
import com.hedvig.app.feature.profile.ui.ProfileViewModel
import com.hedvig.app.feature.profile.ui.referral.InvitesAdapter
import com.hedvig.app.ui.decoration.BottomPaddingItemDecoration
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.showShareSheet
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.interpolateTextKey
import com.hedvig.app.util.safeLet
import kotlinx.android.synthetic.main.fragment_new_referral.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class ReferralsFragment : BaseTabFragment() {
    private val tracker: ProfileTracker by inject()
    private val profileViewModel: ProfileViewModel by sharedViewModel()

    override val layout = R.layout.fragment_new_referral

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        invites.addItemDecoration(
            BottomPaddingItemDecoration(
                resources.getDimensionPixelSize(R.dimen.referral_extra_bottom_space)
            )
        )

        profileViewModel.data.observe(this) { data ->
            safeLet(
                data?.insurance?.cost?.monthlyGross?.amount?.toBigDecimal()?.toInt(),
                data?.referralInformation
            ) { monthlyCost, referralCampaign ->
                bindData(monthlyCost, referralCampaign)
            } ?: Timber.e("No data")

            data?.referralInformation?.campaign?.let { campaign ->
                (campaign.incentive as? ProfileQuery.AsMonthlyCostDeduction)?.let { monthlyCostDeduction ->

                    monthlyCostDeduction.amount?.amount?.toBigDecimal()?.toDouble()?.let { amount ->
                        bindReferralsButton(
                            amount,
                            campaign.code
                        )
                    }
                }
            }
        }
    }

    private fun bindData(monthlyCost: Int, data: ProfileQuery.ReferralInformation) {
        invites.adapter = InvitesAdapter(monthlyCost, data)
    }

    private fun bindReferralsButton(incentive: Double, code: String) {
        referralButton.setHapticClickListener {
            tracker.clickReferral(incentive.toInt())
            requireContext().showShareSheet("TODO Copy") { intent ->
                intent.apply {
                    putExtra(
                        Intent.EXTRA_TEXT,
                        interpolateTextKey(
                            resources.getString(R.string.REFERRAL_SMS_MESSAGE),
                            "REFERRAL_VALUE" to incentive.toBigDecimal().toInt().toString(),
                            "REFERRAL_CODE" to code,
                            "REFERRAL_LINK" to BuildConfig.REFERRALS_LANDING_BASE_URL + code
                        )
                    )
                    type = "text/plain"
                }
            }
        }
    }
}

