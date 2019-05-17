package com.hedvig.common.util.svg

import android.graphics.drawable.PictureDrawable
import android.support.v4.app.Fragment

fun Fragment.buildRequestBuilder() = GlideApp.with(this)
    .`as`(PictureDrawable::class.java)
    .listener(SvgSoftwareLayerSetter())
