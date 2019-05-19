package com.hedvig.logged_in.claims.ui.commonclaim

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.logged_in.R
import com.hedvig.logged_in.claims.ui.commonclaim.bulletpoint.BulletPointsAdapter
import com.hedvig.logged_in.claims.ui.pledge.HonestyPledgeBottomSheet
import com.hedvig.app.BuildConfig
import com.hedvig.common.owldroid.CommonClaimQuery
import com.hedvig.common.owldroid.type.InsuranceStatus
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.common.util.extensions.view.disable
import com.hedvig.common.util.extensions.view.enable
import com.hedvig.common.util.extensions.view.setHapticClickListener
import com.hedvig.common.util.lightenColor
import com.hedvig.common.util.mapppedColor
import com.hedvig.logged_in.util.setupLargeTitle
import kotlinx.android.synthetic.main.common_claim_first_message.*
import kotlinx.android.synthetic.main.fragment_common_claim.*
import com.hedvig.app.R as appR


class CommonClaimFragment : BaseCommonClaimFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_common_claim, container, false)

    override fun bindData(insuranceStatus: InsuranceStatus, data: CommonClaimQuery.CommonClaim) {
        super.bindData(insuranceStatus, data)
        val layout = data.layout() as? CommonClaimQuery.AsTitleAndBulletPoints ?: return
        val backgroundColor = lightenColor(requireContext().compatColor(layout.color().mapppedColor()), 0.3f)
        setupLargeTitle(data.title(), appR.font.circular_bold, appR.drawable.ic_back, backgroundColor) {
            navController.popBackStack()
        }

        commonClaimFirstMessageContainer.setBackgroundColor(backgroundColor)

        commonClaimFirstMessage.text = layout.title()
        commonClaimCreateClaimButton.text = layout.buttonTitle()
        when (insuranceStatus) {
            InsuranceStatus.ACTIVE -> {
                commonClaimCreateClaimButton.enable()
                commonClaimCreateClaimButton.setHapticClickListener {
                    tracker.createClaimClick(data.title())
                    HonestyPledgeBottomSheet
                        .newInstance(data.title(), R.id.action_claimsCommonClaimFragment_to_chatFragment)
                        .show(childFragmentManager, "honestyPledge")
                }
            }
            else -> {
                commonClaimCreateClaimButton.disable()
            }
        }


        bulletPointsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        bulletPointsRecyclerView.adapter =
            BulletPointsAdapter(
                layout.bulletPoints(),
                BuildConfig.BASE_URL,
                requestBuilder
            )
    }
}
