package com.hedvig.app.util.extensions

import android.view.View
import android.view.ViewGroup

fun ViewGroup.addViews(vararg views: View) = views.forEach { addView(it) }

fun ViewGroup.addViews(views: List<View>) = views.forEach { addView(it) }

val ViewGroup.firstChild
    get() = getChildAt(0)

val ViewGroup.children
    get() = ViewIterator(this)

class ViewIterator(private val parent: ViewGroup): Iterator<View> {

    var length = parent.childCount
    var current = 0

    override fun hasNext() = current < length

    override fun next(): View {
        val ret = parent.getChildAt(current)
        current += 1
        return ret
    }
}
