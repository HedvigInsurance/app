package com.hedvig.app.util.extensions

import android.content.Context
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
import com.hedvig.app.ui.fragment.RoundedBottomSheetDialogFragment
import com.hedvig.app.util.extensions.view.setupLargeTitle
import com.hedvig.app.util.whenApiVersion
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.app_bar.view.*

val Fragment.localBroadcastManager get() = LocalBroadcastManager.getInstance(requireContext())

fun Fragment.setupLargeTitle(
    @StringRes title: Int,
    @FontRes font: Int,
    @DrawableRes icon: Int? = null,
    @ColorInt backgroundColor: Int? = null,
    backAction: (() -> Unit)? = null
) {
    setupLargeTitle(getString(title), font, icon, backgroundColor, backAction)
}

fun Fragment.setupLargeTitle(
    title: String,
    @FontRes font: Int,
    @DrawableRes icon: Int? = null,
    @ColorInt backgroundColor: Int? = null,
    backAction: (() -> Unit)? = null
) {
    appBarLayout.setupLargeTitle(title, font, (requireActivity() as AppCompatActivity), icon, backgroundColor, backAction)
}

fun Fragment.makeACall(uri: Uri) {
    val intent = Intent(Intent.ACTION_DIAL)
    intent.data = uri
    startActivity(intent)
}

var Fragment.statusBarColor: Int
    @ColorInt get() = requireActivity().window.statusBarColor
    set(@ColorInt value) {
        requireActivity().window.statusBarColor = value
    }


val Fragment.screenWidth: Int
    get() = requireActivity().screenWidth
