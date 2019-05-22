package com.hedvig.app.util

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.FontRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.content.LocalBroadcastManager
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hedvig.common.ui.fragment.RoundedBottomSheetDialogFragment
import com.hedvig.common.util.extensions.compatFont
import com.hedvig.common.util.whenApiVersion

val Fragment.localBroadcastManager get() = LocalBroadcastManager.getInstance(requireContext())

fun FragmentManager.showBottomSheetDialog(@LayoutRes layout: Int) =
    RoundedBottomSheetDialogFragment.newInstance(layout).show(this, "BottomSheetDialogFragment")

fun Fragment.makeACall(uri: Uri) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = uri
    startActivity(intent)
}

fun Fragment.showShareSheet(title: String, configureClosure: ((Intent) -> Unit)?) {
    val intent = Intent().apply {
        action = Intent.ACTION_SEND
    }

    configureClosure?.let { it(intent) }
    startActivity(
        Intent.createChooser(intent, title)
    )
}

var Fragment.statusBarColor: Int
    @ColorInt get() = requireActivity().window.statusBarColor
    set(@ColorInt value) {
        requireActivity().window.statusBarColor = value
    }
