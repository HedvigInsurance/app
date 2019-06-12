package com.hedvig.app.feature.referrals

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.inputmethod.EditorInfo
import com.hedvig.android.owldroid.type.RedeemCodeStatus
import com.hedvig.app.R
import com.hedvig.app.ui.fragment.RoundedBottomSheetDialogFragment
import com.hedvig.app.util.extensions.hideKeyboard
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.interpolateTextKey
import kotlinx.android.synthetic.main.bottom_sheet_promotion_code.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class PromotionCodeBottomSheet : RoundedBottomSheetDialogFragment() {

    val referralViewModel: ReferralViewModel by sharedViewModel()

    override fun getTheme() = R.style.NoTitleBottomSheetDialogTheme

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_promotion_code, null)
        dialog.setContentView(view)

        dialog.bottomSheetAddPromotionCodeButton.setOnClickListener {
            redeemPromotionCode(dialog.bottomSheetAddPromotionCodeEditText.text.toString())
        }
        dialog.bottomSheetPromotionCodeTermsAndCondition.text = interpolateTextKey(
            getString(R.string.REFERRAL_ADDCOUPON_TC),
            "TERMS_AND_CONDITIONS_LINK" to ""
        )
        dialog.bottomSheetPromotionCodeTermsAndConditionLink.text = getString(R.string.REFERRAL_ADDCOUPON_TERMS_CONDITIONS)
        dialog.bottomSheetPromotionCodeTermsAndConditionLink.setOnClickListener {
            //todo get correct url!!
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hedvig.com/TODO"))
            startActivity(intent)
        }
        dialog.bottomSheetAddPromotionCodeEditText.setOnEditorActionListener { view, actionId, _ ->
            return@setOnEditorActionListener if (actionId == EditorInfo.IME_ACTION_DONE) {
                redeemPromotionCode(dialog.bottomSheetAddPromotionCodeEditText.text.toString())
                view.context.hideKeyboard(view)
                true
            } else {
                false
            }
        }
        referralViewModel.redeemCodeStatus.observe(this) {
            when (it) {
                RedeemCodeStatus.ACCEPTED -> {
                    //TODO trigger refresh of offer screen
                    dismiss()
                }
                else -> {
                    wrongPromotionCode()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        resetErrorState()
    }

    private fun redeemPromotionCode(code: String) {
        resetErrorState()
        referralViewModel.redeemReferralCode(code)
    }

    private fun resetErrorState() {
        dialog.bottomSheetAddPromotionCodeEditText.background = requireContext().getDrawable(R.drawable.background_edit_text_rounded_corners)
        dialog.bottomSheetPromotionCodeMissingCode.remove()
    }

    private fun wrongPromotionCode() {
        dialog.bottomSheetAddPromotionCodeEditText.background = requireContext().getDrawable(R.drawable.background_edit_text_rounded_corners_failed)
        dialog.bottomSheetPromotionCodeMissingCode.show()
    }

    companion object {
        fun newInstance() =
            PromotionCodeBottomSheet()
    }
}
