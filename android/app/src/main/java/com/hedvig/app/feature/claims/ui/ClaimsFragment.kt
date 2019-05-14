package com.hedvig.app.feature.claims.ui

import android.graphics.Rect
import android.graphics.drawable.PictureDrawable
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.bumptech.glide.RequestBuilder
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.type.InsuranceStatus
import com.hedvig.app.BuildConfig
import com.hedvig.app.R
import com.hedvig.app.feature.claims.service.ClaimsTracker
import com.hedvig.app.feature.claims.ui.commonclaim.CommonClaimsAdapter
import com.hedvig.app.feature.claims.ui.pledge.HonestyPledgeBottomSheet
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.proxyNavigate
import com.hedvig.app.util.extensions.setupLargeTitle
import com.hedvig.app.util.extensions.view.disable
import com.hedvig.app.util.extensions.view.enable
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.svg.buildRequestBuilder
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_claims.*
import kotlinx.android.synthetic.main.loading_spinner.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import timber.log.Timber

class ClaimsFragment : Fragment() {
    val tracker: ClaimsTracker by inject()
    val claimsViewModel: ClaimsViewModel by sharedViewModel()

    private val requestBuilder: RequestBuilder<PictureDrawable> by lazy { buildRequestBuilder() }
    private val baseMargin: Int by lazy { resources.getDimensionPixelSize(R.dimen.base_margin) }
    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.rootNavigationHost)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_claims, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(
            R.string.CLAIMS_TITLE,
            R.font.circular_bold,
            backgroundColor = requireContext().compatColor(R.color.off_white)
        )
        appBarLayout.setExpanded(true)

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

        when (commonClaimsData.insurance().status()) {
            InsuranceStatus.ACTIVE -> {
                claimsIllustration.show()
                insuranceInactiveMessage.remove()
                commonClaimCreateClaimButton.enable()
                commonClaimCreateClaimButton.setHapticClickListener {
                    tracker.createClaimClick("main_screen")
                    HonestyPledgeBottomSheet
                        .newInstance("main_screen", R.id.action_loggedInFragment_to_chatFragment)
                        .show(requireFragmentManager(), "honestyPledge")
                }
            }
            else -> {
                claimsIllustration.remove()
                insuranceInactiveMessage.show()
                commonClaimCreateClaimButton.disable()
            }
        }

        // setup common claims
        commonClaimsRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        commonClaimsRecyclerView.adapter =
            CommonClaimsAdapter(
                commonClaims = commonClaimsData.commonClaims(),
                baseUrl = BuildConfig.BASE_URL,
                requestBuilder = requestBuilder,
                navigateToCommonClaimFragment = { commonClaim ->
                    claimsViewModel.setSelectedSubViewData(commonClaim)
                    navController.proxyNavigate(R.id.action_loggedInFragment_to_commonClaimsFragment)
                },
                navigateToEmergencyFragment = { commonClaim ->
                    claimsViewModel.setSelectedSubViewData(commonClaim)
                    navController.proxyNavigate(R.id.action_loggedInFragment_to_emergencyFragment)
                }
            )
        claimsNestedScrollView.scrollTo(0, 0)
    }

    private fun handleNoQuickActions() {
        //TODO: UI
        Timber.i("No claims quick actions found")
    }
}
