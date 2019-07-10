package com.hedvig.app.feature.claims.ui

import android.graphics.Rect
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.view.View
import com.bumptech.glide.RequestBuilder
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.type.InsuranceStatus
import com.hedvig.app.BuildConfig
import com.hedvig.app.R
import com.hedvig.app.feature.claims.service.ClaimsTracker
import com.hedvig.app.feature.claims.ui.commonclaim.CommonClaimActivity
import com.hedvig.app.feature.claims.ui.commonclaim.CommonClaimsAdapter
import com.hedvig.app.feature.claims.ui.commonclaim.CommonClaimsData
import com.hedvig.app.feature.claims.ui.commonclaim.EmergencyActivity
import com.hedvig.app.feature.claims.ui.commonclaim.EmergencyData
import com.hedvig.app.feature.claims.ui.pledge.HonestyPledgeBottomSheet
import com.hedvig.app.feature.loggedin.ui.BaseTabFragment
import com.hedvig.app.util.ReflowText
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.sceneTransitionAnimation
import com.hedvig.app.util.extensions.view.disable
import com.hedvig.app.util.extensions.view.enable
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.svg.buildRequestBuilder
import kotlinx.android.synthetic.main.fragment_claims.*
import kotlinx.android.synthetic.main.loading_spinner.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class ClaimsFragment : BaseTabFragment() {

    private val tracker: ClaimsTracker by inject()
    private val claimsViewModel: ClaimsViewModel by sharedViewModel()

    private val requestBuilder: RequestBuilder<PictureDrawable> by lazy { buildRequestBuilder() }
    private val baseMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin) }

    override val layout = R.layout.fragment_claims

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        claimsViewModel.apply {
            loadingSpinner.show()
            fetchCommonClaims()
            data.observe(this@ClaimsFragment) { commonClaimsData ->
                commonClaimsData?.let {
                    bindData(commonClaimsData)
                } ?: handleNoQuickActions()
            }
        }

        commonClaimsRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)
                val column = position % 2

                outRect.left = column * baseMargin / 2
                outRect.right = baseMargin - (column + 1) * baseMargin / 2
                if (position >= 2) {
                    outRect.top = baseMargin
                }
            }
        })
    }

    private fun bindData(commonClaimsData: CommonClaimQuery.Data) {
        loadingSpinner.remove()
        claimsViewContent.show()

        when (commonClaimsData.insurance.status) {
            InsuranceStatus.ACTIVE -> {
                claimsIllustration.show()
                insuranceInactiveMessage.remove()
                commonClaimCreateClaimButton.enable()
                commonClaimCreateClaimButton.setHapticClickListener {
                    tracker.createClaimClick("main_screen")
                    HonestyPledgeBottomSheet
                        .newInstance("main_screen")
                        .show(childFragmentManager, "honestyPledge")
                }
            }
            else -> {
                claimsIllustration.remove()
                insuranceInactiveMessage.show()
                commonClaimCreateClaimButton.disable()
            }
        }

        // setup common claims
        commonClaimsRecyclerView.adapter =
            CommonClaimsAdapter(
                commonClaims = commonClaimsData.commonClaims,
                baseUrl = BuildConfig.BASE_URL,
                requestBuilder = requestBuilder,
                navigateToCommonClaimFragment = { commonClaim ->
                    CommonClaimsData.from(commonClaim, commonClaimsData.insurance.status)?.let { ccd ->
                        startActivity(CommonClaimActivity.newInstance(requireContext(), ccd))
                    }
                },
                navigateToEmergencyFragment = { commonClaim, holder ->
                    EmergencyData.from(commonClaim, commonClaimsData.insurance.status)?.let { ed ->
                        val intent = EmergencyActivity.newInstance(requireContext(), ed)
                        ReflowText.addExtras(intent, ReflowText.ReflowableTextView(holder.commonClaimLabel))
                        val animation = sceneTransitionAnimation(
                            holder.commonClaimIcon to getString(R.string.common_claim_first_message_icon),
                            holder.commonClaimLabel to getString(R.string.app_bar_title)
                        )
                        startActivity(intent, animation.toBundle())
                    }
                }
            )
        claimsNestedScrollView.scrollTo(0, 0)
    }

    private fun handleNoQuickActions() {
        //TODO: UI
        Timber.i("No claims quick actions found")
    }
}
