package com.hedvig.app.util.extensions

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
import com.hedvig.app.util.whenApiVersion
import kotlinx.android.synthetic.main.app_bar.*

val Fragment.localBroadcastManager get() = LocalBroadcastManager.getInstance(requireContext())

fun FragmentManager.showBottomSheetDialog(@LayoutRes layout: Int) =
    RoundedBottomSheetDialogFragment.newInstance(layout).show(this, "BottomSheetDialogFragment")

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
    (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
    toolbar.title = null // Always remove the underlying toolbar title
    collapsingToolbar.title = title
    val resolvedFont = requireContext().compatFont(font)
    collapsingToolbar.setExpandedTitleTypeface(resolvedFont)
    collapsingToolbar.setCollapsedTitleTypeface(resolvedFont)

    backgroundColor?.let { color ->
        toolbar.setBackgroundColor(color)
        collapsingToolbar.setBackgroundColor(color)
        whenApiVersion(Build.VERSION_CODES.M) {
            val flags = requireActivity().window.decorView.systemUiVisibility
            requireActivity().window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            requireActivity().window.statusBarColor = backgroundColor
        }
    }

    icon?.let { toolbar.setNavigationIcon(it) }
    backAction?.let { toolbar.setNavigationOnClickListener { it() } }
}

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
