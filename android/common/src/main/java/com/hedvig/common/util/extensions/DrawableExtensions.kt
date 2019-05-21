package com.hedvig.common.util.extensions

import android.graphics.drawable.Drawable
import android.support.annotation.ColorInt
import android.support.v4.graphics.drawable.DrawableCompat

fun Drawable.compatSetTint(@ColorInt color: Int) {
    val drawableWrap = DrawableCompat.wrap(this).mutate()
    DrawableCompat.setTint(drawableWrap, color)
}
