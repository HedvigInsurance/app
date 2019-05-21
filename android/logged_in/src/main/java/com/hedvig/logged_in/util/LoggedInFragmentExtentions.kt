package com.hedvig.logged_in.util

import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.FontRes
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.hedvig.common.util.extensions.compatFont
import com.hedvig.common.util.whenApiVersion
import kotlinx.android.synthetic.main.app_bar.*


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
