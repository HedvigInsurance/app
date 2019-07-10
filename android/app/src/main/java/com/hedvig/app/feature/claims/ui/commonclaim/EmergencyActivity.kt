package com.hedvig.app.feature.claims.ui.commonclaim

import android.app.SharedElementCallback
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hedvig.android.owldroid.graphql.CommonClaimQuery
import com.hedvig.android.owldroid.type.HedvigColor
import com.hedvig.android.owldroid.type.InsuranceStatus
import com.hedvig.app.BuildConfig
import com.hedvig.app.R
import com.hedvig.app.feature.claims.service.ClaimsTracker
import com.hedvig.app.feature.claims.ui.ClaimsViewModel
import com.hedvig.app.util.ReflowText
import com.hedvig.app.util.extensions.*
import com.hedvig.app.util.extensions.view.disable
import com.hedvig.app.util.extensions.view.enable
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.lightenColor
import com.hedvig.app.util.mappedColor
import com.hedvig.app.util.svg.buildRequestBuilder
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.common_claim_first_message.*
import kotlinx.android.synthetic.main.activity_emergency.*
import org.koin.android.ext.android.inject

class EmergencyActivity : AppCompatActivity() {

    private val claimsViewModel: ClaimsViewModel by inject()
    private val tracker: ClaimsTracker by inject()
    private val requestBuilder by lazy { buildRequestBuilder() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_emergency)

        val data = intent.getParcelableExtra<EmergencyData>(EMERGENCY_DATA)

        requestBuilder
            .load(Uri.parse(BuildConfig.BASE_URL + data.iconUrl))
            .into(commonClaimFirstMessageIcon)

        val backgroundColor = lightenColor(compatColor(data.color.mappedColor()), 0.3f)
        window.statusBarColor = backgroundColor

        screenTitle.text = data.title

        setEnterSharedElementCallback(object : SharedElementCallback() {
            override fun onSharedElementStart(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                ReflowText.setupReflow(intent, screenTitle)
            }

            override fun onSharedElementEnd(
                sharedElementNames: MutableList<String>?,
                sharedElements: MutableList<View>?,
                sharedElementSnapshots: MutableList<View>?
            ) {
                ReflowText.setupReflow(ReflowText.ReflowableTextView(screenTitle))
            }
        })

        goBack.setOnClickListener {
            onBackPressed()
        }

        commonClaimFirstMessageContainer.setBackgroundColor(backgroundColor)

        commonClaimFirstMessage.text = getString(R.string.CLAIMS_EMERGENCY_FIRST_MESSAGE)
        commonClaimCreateClaimButton.remove()

        when (data.insuranceStatus) {
            InsuranceStatus.ACTIVE -> showInsuranceActive()
            else -> showInsuranceInactive()
        }

        thirdEmergencyButton.setHapticClickListener {
            tracker.emergencyChat()
            claimsViewModel.triggerFreeTextChat {
                startClosableChat()
            }
        }
    }

    private fun showInsuranceActive() {
        firstEmergencyButton.enable()
        secondEmergencyButton.enable()

        firstEmergencyButton.setHapticClickListener {
            tracker.emergencyClick()
            claimsViewModel.triggerCallMeChat {
                startClosableChat()
            }
        }
        secondEmergencyButton.setHapticClickListener {
            tracker.callGlobalAssistance()
            makeACall(GLOBAL_ASSISTANCE_URI)
        }
    }

    private fun showInsuranceInactive() {
        firstEmergencyButton.disable()
        secondEmergencyButton.disable()
    }

    companion object {
        private val GLOBAL_ASSISTANCE_URI = Uri.parse("tel:+4538489461")

        private const val EMERGENCY_DATA = "emergency_data"

        fun newInstance(context: Context, data: EmergencyData) = Intent(context, EmergencyActivity::class.java).apply {
            putExtra(EMERGENCY_DATA, data)
        }
    }
}

@Parcelize
data class EmergencyData(
    val iconUrl: String,
    val color: HedvigColor,
    val title: String,
    val insuranceStatus: InsuranceStatus
) : Parcelable {
    companion object {
        fun from(data: CommonClaimQuery.CommonClaim, status: InsuranceStatus): EmergencyData? {
            val layout = data.layout as? CommonClaimQuery.AsEmergency ?: return null
            return EmergencyData(
                data.icon.svgUrl,
                layout.color,
                data.title,
                status
            )
        }
    }
}

