package com.hedvig.app.ui.fragment

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialog
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewTreeObserver
import com.hedvig.app.R
import com.hedvig.app.util.extensions.view.show
import com.hedvig.app.util.extensions.view.updatePadding
import timber.log.Timber

abstract class RoundedBottomSheetDialogFragment : BottomSheetDialogFragment() {

    override fun getTheme(): Int = R.style.BottomSheetDialogTheme

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog = BottomSheetDialog(requireContext(), theme)
}
