package com.hedvig.app.feature.chat.native

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.*
import com.hedvig.app.R
import com.hedvig.app.util.whenApiVersion
import kotlinx.android.synthetic.main.attach_picker_dialog.*

class AttachPicker(context: Context) : Dialog(context, R.style.TransparentDialog) {

    var pickerHeight = 0

    init {
        window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window?.setWindowAnimations(R.style.DialogNoAnimation)
        setContentView(R.layout.attach_picker_dialog)
        setupDialogTouchEvents()
        setupWindowsParams()
        setupBottomSheetParams()
    }

    private fun setupWindowsParams() = window?.let { window ->
        val params = window.attributes
        params.width = ViewGroup.LayoutParams.MATCH_PARENT
        params.height = ViewGroup.LayoutParams.MATCH_PARENT

        params.dimAmount = 0f
        params.flags = params.flags and WindowManager.LayoutParams.FLAG_DIM_BEHIND.inv()

        params.flags = params.flags or
            WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR or
            WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS

        whenApiVersion(Build.VERSION_CODES.P) {
            params.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
        window.attributes = params
    }

    private fun setupBottomSheetParams() {
        val params = attachPickerBottomSheet.layoutParams
        params.height = pickerHeight
        attachPickerBottomSheet.layoutParams = params
    }

    private fun setupDialogTouchEvents() {
        attachPickerRoot.setOnTouchListener { _, _ ->
            dismiss()
            false
        }
        //prevent dismiss in this area
        attachPickerBottomSheet.setOnTouchListener { _, _ -> true }
    }
}
