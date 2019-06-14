package com.hedvig.app.ui.decoration

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class BottomPaddingItemDecoration(val bottomPadding: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view)

        val lastPosition = (parent.adapter?.itemCount ?: 0) - 1
        if (position == lastPosition) {
            outRect.bottom = bottomPadding
        } else {
            super.getItemOffsets(outRect, view, parent, state)
        }
    }
}
