package com.hedvig.app.feature.claims.ui.pledge

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.LayoutInflater
import androidx.navigation.findNavController
import com.hedvig.app.R
import com.hedvig.app.feature.chat.ChatActivity
import com.hedvig.app.feature.claims.service.ClaimsTracker
import com.hedvig.app.feature.claims.ui.ClaimsViewModel
import com.hedvig.app.ui.fragment.RoundedBottomSheetDialogFragment
import com.hedvig.app.util.extensions.proxyNavigate
import com.hedvig.app.util.extensions.view.setHapticClickListener
import kotlinx.android.synthetic.main.bottom_sheet_honesty_pledge.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class HonestyPledgeBottomSheet : RoundedBottomSheetDialogFragment() {
    val tracker: ClaimsTracker by inject()

    val claimsViewModel: ClaimsViewModel by sharedViewModel()

    override fun getTheme() = R.style.NoTitleBottomSheetDialogTheme

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_honesty_pledge, null)
        dialog.setContentView(view)

        dialog.bottomSheetHonestyPledgeButton.setHapticClickListener {
            tracker.pledgeHonesty(arguments?.getString(ARGS_CLAIM_KEY))
            claimsViewModel.triggerClaimsChat {
                dismiss()
                val intent = Intent(requireContext(), ChatActivity::class.java)
                intent.putExtra(ChatActivity.ARGS_SHOW_CLOSE, true)
                startActivity(intent)
            }
        }
    }

    companion object {
        private const val ARGS_CLAIM_KEY = "claim_key"

        fun newInstance(claimKey: String): HonestyPledgeBottomSheet {
            val arguments = Bundle().apply {
                putString(ARGS_CLAIM_KEY, claimKey)
            }

            return HonestyPledgeBottomSheet()
                .also { it.arguments = arguments }
        }
    }
}
