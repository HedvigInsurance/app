package com.hedvig.app.util.svg

import android.app.Activity
import android.graphics.drawable.PictureDrawable
import android.support.v4.app.Fragment

fun Fragment.buildRequestBuilder() = GlideApp.with(this)
    .`as`(PictureDrawable::class.java)
    .listener(SvgSoftwareLayerSetter())

fun Activity.buildRequestBuilder() = GlideApp.with(this)
    .`as`(PictureDrawable::class.java)
    .listener(SvgSoftwareLayerSetter())
