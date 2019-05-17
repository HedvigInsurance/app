package com.hedvig.common.util.extensions

import android.view.View
import android.view.ViewGroup

fun ViewGroup.addViews(vararg views: View) = views.forEach { addView(it) }

fun ViewGroup.addViews(views: List<View>) = views.forEach { addView(it) }
