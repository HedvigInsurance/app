package com.hedvig.logged_in.claims.ui.commonclaim

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.View
import androidx.navigation.findNavController
import com.bumptech.glide.RequestBuilder
import com.hedvig.app.BuildConfig
import com.hedvig.logged_in.R
import com.hedvig.logged_in.claims.service.ClaimsTracker
import com.hedvig.logged_in.claims.ui.ClaimsViewModel
import com.hedvig.common.owldroid.CommonClaimQuery
import com.hedvig.common.owldroid.type.InsuranceStatus
import com.hedvig.common.util.extensions.observe
import com.hedvig.common.util.svg.buildRequestBuilder
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.common_claim_first_message.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

abstract class BaseCommonClaimFragment : Fragment() {
    val tracker: ClaimsTracker by inject()
    val claimsViewModel: ClaimsViewModel by sharedViewModel()

    val requestBuilder: RequestBuilder<PictureDrawable> by lazy { buildRequestBuilder() }

    val navController by lazy { requireActivity().findNavController(R.id.loggedInNavigationHost) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        claimsViewModel.selectedSubViewData.observe(this) { commonClaim ->
            claimsViewModel.data.value?.insurance()?.status()?.let { insuranceStatus ->
                commonClaim?.let { bindData(insuranceStatus, it) }
            }
        }
    }

    @CallSuper
    open fun bindData(insuranceStatus: InsuranceStatus, data: CommonClaimQuery.CommonClaim) {
        appBarLayout.setExpanded(false, false)
        requestBuilder.load(Uri.parse(BuildConfig.BASE_URL + data.icon().svgUrl())).into(commonClaimFirstMessageIcon)
    }
}


