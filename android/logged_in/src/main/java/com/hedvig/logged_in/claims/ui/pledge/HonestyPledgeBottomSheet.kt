package com.hedvig.logged_in.claims.ui.pledge

import android.app.Dialog
import android.os.Bundle
import android.support.annotation.IdRes
import android.view.LayoutInflater
import androidx.navigation.findNavController
import com.hedvig.logged_in.R
import com.hedvig.logged_in.claims.service.ClaimsTracker
import com.hedvig.logged_in.claims.ui.ClaimsViewModel
import com.hedvig.common.constants.FragmentArgs
import com.hedvig.common.ui.fragment.RoundedBottomSheetDialogFragment
import com.hedvig.common.util.extensions.proxyNavigate
import com.hedvig.common.util.extensions.view.setHapticClickListener
import kotlinx.android.synthetic.main.bottom_sheet_honesty_pledge.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel
import com.hedvig.app.R as appR


class HonestyPledgeBottomSheet : RoundedBottomSheetDialogFragment() {
    val tracker: ClaimsTracker by inject()

    val claimsViewModel: ClaimsViewModel by sharedViewModel()

    val navController by lazy { requireActivity().findNavController(R.id.loggedInNavigationHost) }

    override fun getTheme() = appR.style.NoTitleBottomSheetDialogTheme

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_honesty_pledge, null)
        dialog.setContentView(view)

        dialog.bottomSheetHonestyPledgeButton.setHapticClickListener {
            tracker.pledgeHonesty(arguments?.getString(ARGS_CLAIM_KEY))
            claimsViewModel.triggerClaimsChat {
                dismiss()
                arguments?.getInt(ARGS_NAVIGATION_ACTION)?.let {
                    navController.proxyNavigate(it, Bundle().apply {
                        putBoolean(FragmentArgs.ARGS_SHOW_CLOSE, true)
                    })
                }
            }
        }
    }

    companion object {
        private const val ARGS_CLAIM_KEY = "claim_key"
        private const val ARGS_NAVIGATION_ACTION = "navigation_action"

        fun newInstance(claimKey: String, @IdRes navigationAction: Int): HonestyPledgeBottomSheet {
            val arguments = Bundle().apply {
                putString(ARGS_CLAIM_KEY, claimKey)
                putInt(ARGS_NAVIGATION_ACTION, navigationAction)
            }

            return HonestyPledgeBottomSheet()
                .also { it.arguments = arguments }
        }
    }
}
