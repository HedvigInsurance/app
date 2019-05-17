package com.hedvig.app.feature.claims.ui.commonclaim

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.hedvig.app.R
import com.hedvig.common.owldroid.CommonClaimQuery
import com.hedvig.app.feature.chat.ChatFragment
import com.hedvig.common.owldroid.type.InsuranceStatus
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.app.util.makeACall
import com.hedvig.common.util.extensions.proxyNavigate
import com.hedvig.app.util.setupLargeTitle
import com.hedvig.common.util.extensions.view.disable
import com.hedvig.common.util.extensions.view.enable
import com.hedvig.common.util.extensions.view.remove
import com.hedvig.common.util.extensions.view.setHapticClickListener
import com.hedvig.common.util.mapppedColor
import kotlinx.android.synthetic.main.common_claim_first_message.*
import kotlinx.android.synthetic.main.fragment_emergency.*

class EmergencyFragment : BaseCommonClaimFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_emergency, container, false)

    override fun bindData(insuranceStatus: InsuranceStatus, data: CommonClaimQuery.CommonClaim) {
        super.bindData(insuranceStatus, data)
        val layout = data.layout() as? CommonClaimQuery.AsEmergency ?: return
        val backgroundColor = requireContext().compatColor(layout.color().mapppedColor())
        setupLargeTitle(data.title(), R.font.circular_bold, R.drawable.ic_back, backgroundColor) {
            navController.popBackStack()
        }
        commonClaimFirstMessageContainer.setBackgroundColor(backgroundColor)

        commonClaimFirstMessage.text = getString(R.string.CLAIMS_EMERGENCY_FIRST_MESSAGE)
        commonClaimCreateClaimButton.remove()

        when (insuranceStatus) {
            InsuranceStatus.ACTIVE -> showInsuranceActive()
            else -> showInsuranceInactive()
        }

        thirdEmergencyButton.setHapticClickListener {
            tracker.emergencyChat()
            claimsViewModel.triggerFreeTextChat {
                navigateToChat()
            }
        }
    }

    private fun showInsuranceActive() {
        firstEmergencyButton.enable()
        secondEmergencyButton.enable()

        firstEmergencyButton.setHapticClickListener {
            tracker.emergencyClick()
            claimsViewModel.triggerCallMeChat {
                navigateToChat()
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

    private fun navigateToChat() =
        navController.proxyNavigate(
            R.id.action_claimsEmergencyFragment_to_chatFragment,
            Bundle().apply { putBoolean(ChatFragment.ARGS_SHOW_CLOSE, true) }
        )

    companion object {
        private val GLOBAL_ASSISTANCE_URI = Uri.parse("tel:+4538489461")
    }
}


