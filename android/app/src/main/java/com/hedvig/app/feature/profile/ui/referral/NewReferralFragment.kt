package com.hedvig.app.feature.profile.ui.referral

import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.RecyclerView
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
import com.hedvig.app.util.interpolateTextKey
import kotlinx.android.synthetic.main.fragment_new_referral.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import java.math.BigDecimal

class NewReferralFragment : Fragment() {

    val tracker: ProfileTracker by inject()

    val profileViewModel: ProfileViewModel by sharedViewModel()

    private var buttonAnimator: ValueAnimator? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_new_referral, container, false)

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
        invites.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val position = parent.getChildAdapterPosition(view)

                val lastPosition = (parent.adapter?.itemCount ?: 0) - 1
                if (position == lastPosition) {
                    outRect.bottom = parent.resources.getDimensionPixelSize(R.dimen.referral_extra_bottom_space)
                } else {
                    super.getItemOffsets(outRect, view, parent, state)
                }
            }
        })

        bindData(mockData)
    }

    private fun bindData(data: MockData) {
        invites.adapter = InvitesAdapter(data)
        referralButton.setHapticClickListener {
            tracker.clickReferral(data.referralInformation.discount.amount.toInt())
            showShareSheet("TODO Copy") { intent ->
                intent.apply {
                    putExtra(
                        Intent.EXTRA_TEXT,
                        interpolateTextKey(
                            resources.getString(R.string.REFERRAL_SMS_MESSAGE),
                            "REFERRAL_VALUE" to data.referralInformation.discount.amount.toString(),
                            "REFERRAL_CODE" to data.referralInformation.code,
                            "REFERRAL_LINK" to data.referralInformation.link.toString()
                        )
                    )
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
        private val tenSek = MockMonetaryAmount(
            BigDecimal.TEN,
            "SEK"
        )

        private val mockData = MockData(
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
                MockReferral("Oscar", MockReferralStatus.ACTIVE, tenSek),
                MockReferral("Sam", MockReferralStatus.INITIATED, tenSek),
                MockReferral("Fredrik", MockReferralStatus.ACTIVE, tenSek),
                MockReferral("Alex", MockReferralStatus.ACTIVE, tenSek),
                MockReferral("Meletis", MockReferralStatus.TERMINATED, tenSek)
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
