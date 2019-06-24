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

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private var rootView: View? = null

    var extraPaddingOnExpand = 0
    var defaultPadding = 0

    private val globalLayoutListener = ViewTreeObserver.OnPreDrawListener {
        val rect = Rect()
        val decorView = requireActivity().window.decorView
        decorView.getWindowVisibleDisplayFrame(rect)

        val height = requireContext().resources.displayMetrics.heightPixels
        val diff = height - rect.bottom

        if (diff != 0) {
            if (rootView?.paddingBottom != diff - extraPaddingOnExpand) {
                rootView?.updatePadding(bottom = diff - extraPaddingOnExpand)
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        } else {
            if (rootView?.paddingBottom != defaultPadding) {
                rootView?.updatePadding(bottom = defaultPadding)
            }
        }
        true
    }

    fun handleExpandWithKeyboard(rootView: View, extraPaddingOnExpand: Int, defaultPadding: Int) {
        this.extraPaddingOnExpand = extraPaddingOnExpand
        this.defaultPadding = defaultPadding
        bottomSheetBehavior = BottomSheetBehavior.from<View>(rootView.parent as View)
        requireActivity().window.decorView.viewTreeObserver.addOnPreDrawListener(globalLayoutListener)
        this.rootView = rootView
    }

    override fun onDestroy() {
        requireActivity().window.decorView.viewTreeObserver.removeOnPreDrawListener(globalLayoutListener)
        super.onDestroy()
    }
}
