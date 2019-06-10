package com.hedvig.app.feature.referrals

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Toast
import com.hedvig.app.R
import com.hedvig.app.ui.fragment.RoundedBottomSheetDialogFragment
import com.hedvig.app.util.interpolateTextKey
import kotlinx.android.synthetic.main.bottom_sheet_promotion_code.*

class PromotionCodeBottomSheet : RoundedBottomSheetDialogFragment() {

    override fun getTheme() = R.style.NoTitleBottomSheetDialogTheme

    override fun setupDialog(dialog: Dialog, style: Int) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.bottom_sheet_promotion_code, null)
        dialog.setContentView(view)

        dialog.bottomSheetAddPromotionCodeButton.setOnClickListener {
            addPromotionCode(dialog.bottomSheetAddPromotionCodeEditText.text.toString())
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
    }

    private fun addPromotionCode(code: String) {
        Toast.makeText(requireContext(), "Send $code", Toast.LENGTH_SHORT).show()
        dialog.bottomSheetAddPromotionCodeEditText.background = requireContext().getDrawable(R.drawable.background_edit_text_rounded_corners_failed)
    }

    companion object {
        fun newInstance() =
            PromotionCodeBottomSheet()
    }
}
