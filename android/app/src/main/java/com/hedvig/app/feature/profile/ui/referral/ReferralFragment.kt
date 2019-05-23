package com.hedvig.app.feature.profile.ui.referral

import android.animation.ValueAnimator
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.app.R
import com.hedvig.app.feature.profile.service.ProfileTracker
import com.hedvig.app.feature.profile.ui.ProfileViewModel
import com.hedvig.app.util.extensions.compatDrawable
import com.hedvig.app.util.extensions.setupLargeTitle
import com.hedvig.app.util.extensions.showShareSheet
import com.hedvig.app.util.extensions.view.setHapticClickListener
import kotlinx.android.synthetic.main.fragment_referral.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.math.BigDecimal

class ReferralFragment : Fragment() {

    val tracker: ProfileTracker by inject()

    val profileViewModel: ProfileViewModel by sharedViewModel()

    private var buttonAnimator: ValueAnimator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_referral, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(R.string.PROFILE_REFERRAL_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            requireActivity().findNavController(R.id.rootNavigationHost).popBackStack()
        }
        referralButton.setCompoundDrawablesRelativeWithIntrinsicBounds(
            null,
            null,
            requireContext().compatDrawable(R.drawable.icon_share_white),
            null
        )

        bindData(mockData)
    }

    private fun bindData(data: MockData) {
        invites.adapter = InvitesAdapter(data)
        referralButton.setHapticClickListener {
            tracker.clickReferral(data.referralInformation.discount.amount.toInt())
            showShareSheet("TODO Copy") { intent ->
                intent.apply {
                    putExtra(Intent.EXTRA_TEXT, "TODO Copy")
                    type = "text/plain"
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        buttonAnimator?.removeAllListeners()
        buttonAnimator?.cancel()
    }

    companion object {
        val tenSek = MockMonetaryAmount(
            BigDecimal.TEN,
            "SEK"
        )

        val mockData = MockData(
            MockReferralInformation(
                "HDVG87",
                Uri.parse("https://hedvigdev.page.link/HDVG87"),
                tenSek
            ),
            MockReferral(
                "Tester",
                MockReferralStatus.ACTIVE,
                tenSek
            ),
            listOf(
                MockReferral(null, MockReferralStatus.NOT_INITIATED, tenSek),
                MockReferral("Oscar", MockReferralStatus.ACTIVE, tenSek)
            )
        )
    }
}

data class MockData(
    val referralInformation: MockReferralInformation,
    val sender: MockReferral,
    val receivers: List<MockReferral>
)

data class MockReferralInformation(
    val code: String,
    val link: Uri,
    val discount: MockMonetaryAmount
)

data class MockMonetaryAmount(
    val amount: BigDecimal,
    val currency: String
)

data class MockReferral(
    val name: String?,
    val status: MockReferralStatus,
    val discount: MockMonetaryAmount
)

enum class MockReferralStatus {
    ACTIVE,
    IN_PROGRESS,
    INITIATED,
    NOT_INITIATED,
    TERMINATED
}
