package com.hedvig.app.feature.referrals

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import androidx.navigation.findNavController
import com.hedvig.app.MainActivity
import com.hedvig.app.R
import com.hedvig.app.feature.profile.service.ProfileTracker
import com.hedvig.app.feature.profile.ui.ProfileViewModel
import com.hedvig.app.feature.profile.ui.referral.*
import com.hedvig.app.ui.decoration.BottomPaddingItemDecoration
import com.hedvig.app.util.extensions.*
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.interpolateTextKey
import kotlinx.android.synthetic.main.fragment_new_referral.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.math.BigDecimal

class ReferralsActivity : AppCompatActivity() {

    val tracker: ProfileTracker by inject()

    val profileViewModel: ProfileViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_new_referral)

        setupLargeTitle(R.string.PROFILE_REFERRAL_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            onBackPressed()
        }
        invites.addItemDecoration(
            BottomPaddingItemDecoration(
                resources.getDimensionPixelSize(R.dimen.referral_extra_bottom_space)
            )
        )

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

    companion object {

        const val EXTRA_IS_FROM_REFERRALS_NOTIFICATION = "extra_is_from_referrals_notification"

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

    override fun onBackPressed() {
        if (intent.getBooleanExtra(EXTRA_IS_FROM_REFERRALS_NOTIFICATION, false)) {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = intent.flags or Intent.FLAG_ACTIVITY_NO_HISTORY
            intent.putExtra(MainActivity.EXTRA_NAVIGATE_TO_PROFILE_ON_START_UP, true)
            startActivity(intent)
        } else {
            super.onBackPressed()
        }
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
