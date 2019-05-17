package com.hedvig.common.util.extensions.view

import android.graphics.Rect
import android.support.annotation.Dimension
import android.view.HapticFeedbackConstants
import android.view.TouchDelegate
import android.view.View
import android.view.ViewTreeObserver

fun View.show(): View {
    if (visibility != View.VISIBLE) {
        visibility = View.VISIBLE
    }
    return this
}

fun View.hide(): View {
    if (visibility != View.INVISIBLE) {
        visibility = View.INVISIBLE
    }

    return this
}

fun View.remove(): View {
    if (visibility != View.GONE) {
        this.visibility = View.GONE
    }

    return this
}

fun View.disable() {
    isEnabled = false
    alpha = 0.2f
}

fun View.enable() {
    isEnabled = true
    alpha = 1f
}

fun View.increaseTouchableArea(additionalArea: Int): View {
    val parent = (this.parent as View)
    parent.post {
        val touchableArea = Rect()
        getHitRect(touchableArea)
        touchableArea.top -= additionalArea
        touchableArea.left -= additionalArea
        touchableArea.right += additionalArea
        touchableArea.bottom += additionalArea
        parent.touchDelegate = TouchDelegate(touchableArea, this)
    }

    return this
}

inline fun View.doOnLayout(crossinline action: () -> Unit) =
    viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            viewTreeObserver.removeOnGlobalLayoutListener(this)
            action()
        }
    })

fun View.setHapticClickListener(onClickListener: (View) -> Unit) {
    setOnClickListener { view ->
        performOnTapHapticFeedback()
        onClickListener(view)
    }
}

fun View.performOnTapHapticFeedback() = performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)

fun View.updatePadding(
    @Dimension start: Int? = null,
    @Dimension top: Int? = null,
    @Dimension end: Int? = null,
    @Dimension bottom: Int? = null
) = setPaddingRelative(
    start ?: paddingStart,
    top ?: paddingTop,
    end ?: paddingEnd,
    bottom ?: paddingBottom

)
