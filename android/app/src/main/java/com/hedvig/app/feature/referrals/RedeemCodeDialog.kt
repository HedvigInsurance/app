package com.hedvig.app.feature.referrals

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import com.hedvig.app.R
import com.hedvig.app.react.ActivityStarterModule
import com.hedvig.app.util.extensions.hideKeyboard
import com.hedvig.app.util.extensions.localBroadcastManager
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.extensions.observe
import kotlinx.android.synthetic.main.promotion_code_dialog.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class RedeemCodeDialog : DialogFragment() {

    private val referralViewModel: ReferralViewModel by sharedViewModel()

    private val tracker: ReferralsTracker by inject()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.promotion_code_dialog, null)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.setContentView(view)

        dialog.bottomSheetAddPromotionCodeButton.setHapticClickListener {
            redeemPromotionCode(dialog.bottomSheetAddPromotionCodeEditText.text.toString())
        }
        dialog.bottomSheetPromotionCodeTermsAndConditionLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hedvig.com/invite/terms"))
            startActivity(intent)
        }
        dialog.bottomSheetAddPromotionCodeEditText.setOnEditorActionListener { v, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                redeemPromotionCode(dialog.bottomSheetAddPromotionCodeEditText.text.toString())
                view.context.hideKeyboard(v)
                true
            } else {
                false
            }
        }
        referralViewModel.redeemCodeStatus.observe(this) {
            localBroadcastManager.sendBroadcast(Intent(ActivityStarterModule.REDEEMED_CODE_BROADCAST).apply {
                putExtra(
                    ActivityStarterModule.BROADCAST_MESSAGE_NAME,
                    ActivityStarterModule.MESSAGE_PROMOTION_CODE_REDEEMED
                )
            })
            dismiss()
        }
        return dialog
    }

    override fun onResume() {
        super.onResume()
        resetErrorState()
    }

    private fun redeemPromotionCode(code: String) {
        tracker.redeemReferralCodeOverlay()
        resetErrorState()
        referralViewModel.redeemReferralCode(code)
    }

    private fun resetErrorState() {
        dialog.bottomSheetAddPromotionCodeEditText.background =
            requireContext().getDrawable(R.drawable.background_edit_text_rounded_corners)
        dialog.bottomSheetPromotionCodeMissingCode.remove()
    }

    private fun wrongPromotionCode() {
        dialog.bottomSheetAddPromotionCodeEditText.background =
            requireContext().getDrawable(R.drawable.background_edit_text_rounded_corners_failed)
        dialog.bottomSheetPromotionCodeMissingCode.show()
    }

    companion object {
        const val TAG = "redeemCodeBottomSheet"

        fun newInstance() =
            RedeemCodeDialog()
    }
}
