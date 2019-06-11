package com.hedvig.app.util.extensions

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.support.annotation.ColorInt
import android.support.annotation.DrawableRes
import android.support.annotation.FontRes
import android.support.annotation.StringRes
import android.support.v7.app.AppCompatActivity
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowManager
import com.hedvig.app.R
import com.hedvig.app.util.extensions.view.setupLargeTitle
import com.hedvig.app.util.hasNotch
import com.hedvig.app.util.whenApiVersion
import kotlinx.android.synthetic.main.app_bar.*
import com.hedvig.app.feature.chat.ChatActivity
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat

fun Activity.setLightNavigationBar() {
    window.navigationBarColor = compatColor(R.color.off_white)
    whenApiVersion(Build.VERSION_CODES.O) {
        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
}

fun Activity.setDarkNavigationBar() {
    window.navigationBarColor = compatColor(R.color.black)
    whenApiVersion(Build.VERSION_CODES.O) {
        val flags = window.decorView.systemUiVisibility
        window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR
    }
}

fun Activity.showStatusBar() {
    whenApiVersion(Build.VERSION_CODES.M) {
        if (hasNotch()) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = compatColor(R.color.off_white)
            return
        }
    }

    window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Activity.hideStatusBar() {
    whenApiVersion(Build.VERSION_CODES.M) {
        if (hasNotch()) {
            val flags = window.decorView.systemUiVisibility
            window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = compatColor(R.color.black)
            return
        }
    }

    window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
}

fun Activity.setLightStatusBarText() {
    val flags = window.decorView.systemUiVisibility
    window.decorView.systemUiVisibility = flags xor View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
}

val Activity.displayMetrics: DisplayMetrics
    get() {
        val metrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(metrics)
        return metrics
    }

val Activity.screenWidth: Int
    get() = window.decorView.measuredWidth

fun AppCompatActivity.setupLargeTitle(
    @StringRes title: Int,
    @FontRes font: Int,
    @DrawableRes icon: Int? = null,
    @ColorInt backgroundColor: Int? = null,
    backAction: (() -> Unit)? = null
) {
    setupLargeTitle(
        getString(title),
        font,
        icon,
        backgroundColor,
        backAction
    )
}

fun AppCompatActivity.setupLargeTitle(
    title: String,
    @FontRes font: Int,
    @DrawableRes icon: Int? = null,
    @ColorInt backgroundColor: Int? = null,
    backAction: (() -> Unit)? = null
) {
    appBarLayout.setupLargeTitle(
        title,
        font,
        this,
        icon,
        backgroundColor,
        backAction
    )
}

val Activity.localBroadcastManager get() = android.support.v4.content.LocalBroadcastManager.getInstance(this)

fun Activity.startClosableChat() {
    val intent = Intent(this, ChatActivity::class.java)
    intent.putExtra(ChatActivity.EXTRA_SHOW_CLOSE, true)

    val options =
        ActivityOptionsCompat.makeCustomAnimation(this, R.anim.activity_slide_up_in, R.anim.stay_in_place)

    ActivityCompat.startActivity(this, intent, options.toBundle())
}
