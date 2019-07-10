package com.hedvig.app.feature.claims.ui.commonclaim

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.type.HedvigColor
import com.hedvig.android.owldroid.type.InsuranceStatus
import com.hedvig.app.BuildConfig
import com.hedvig.app.R
import com.hedvig.app.feature.claims.service.ClaimsTracker
import com.hedvig.app.feature.claims.ui.commonclaim.bulletpoint.BulletPointsAdapter
import com.hedvig.app.feature.claims.ui.pledge.HonestyPledgeBottomSheet
import com.hedvig.app.util.extensions.compatColor
import com.hedvig.app.util.extensions.setupLargeTitle
import com.hedvig.app.util.extensions.view.disable
import com.hedvig.app.util.extensions.view.enable
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.lightenColor
import com.hedvig.app.util.mappedColor
import com.hedvig.app.util.svg.buildRequestBuilder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.common_claim_first_message.*
import kotlinx.android.synthetic.main.activity_common_claim.*
import org.koin.android.ext.android.inject

class CommonClaimActivity : AppCompatActivity() {

    private val tracker: ClaimsTracker by inject()
    private val requestBuilder by lazy { buildRequestBuilder() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_common_claim)

        appBarLayout.setExpanded(false, false)
        val data = intent.getParcelableExtra<CommonClaimsData>(CLAIMS_DATA)

        val backgroundColor = lightenColor(compatColor(data.color.mappedColor()), 0.3f)
        setupLargeTitle(data.title, R.font.circular_bold, R.drawable.ic_back, backgroundColor) {
            onBackPressed()
        }

        requestBuilder
            .load(Uri.parse(BuildConfig.BASE_URL + data.iconUrl))
            .into(commonClaimFirstMessageIcon)

        commonClaimFirstMessageContainer.setBackgroundColor(backgroundColor)

        commonClaimFirstMessage.text = data.title
        commonClaimCreateClaimButton.text = data.buttonText

        when (data.insuranceStatus) {
            InsuranceStatus.ACTIVE -> {
                commonClaimCreateClaimButton.enable()
                commonClaimCreateClaimButton.setHapticClickListener {
                    tracker.createClaimClick(data.title)
                    HonestyPledgeBottomSheet
                        .newInstance(data.title)
                        .show(supportFragmentManager, HonestyPledgeBottomSheet.TAG)
                }
            }
            else -> {
                commonClaimCreateClaimButton.disable()
            }
        }
        bulletPointsRecyclerView.adapter =
            BulletPointsAdapter(
                data.bulletPoints,
                BuildConfig.BASE_URL,
                requestBuilder
            )
    }

    companion object {
        private const val CLAIMS_DATA = "claims_Data"

        fun newInstance(context: Context, data: CommonClaimsData) =
            Intent(context, CommonClaimActivity::class.java).apply {
                putExtra(CLAIMS_DATA, data)
            }
    }
}

@Parcelize
data class CommonClaimsData(
    val iconUrl: String,
    val title: String,
    val color: HedvigColor,
    val buttonText: String,
    val insuranceStatus: InsuranceStatus,
    val bulletPoints: List<BulletPoint>
) : Parcelable {
    companion object {
        fun from(data: CommonClaimQuery.CommonClaim, insuranceStatus: InsuranceStatus): CommonClaimsData? {
            val layout = (data.layout as? CommonClaimQuery.AsTitleAndBulletPoints) ?: return null
            return CommonClaimsData(
                data.icon.svgUrl,
                data.title,
                layout.color,
                layout.buttonTitle,
                insuranceStatus,
                BulletPoint.from(layout.bulletPoints)
            )
        }
    }
}

@Parcelize
data class BulletPoint(
    val title: String,
    val description: String,
    val iconUrl: String
) : Parcelable {
    companion object {
        fun from(bulletPoints: List<CommonClaimQuery.BulletPoint>) = bulletPoints.map { bp ->
            BulletPoint(
                bp.title,
                bp.description,
                bp.icon.svgUrl
            )
        }
    }
}
