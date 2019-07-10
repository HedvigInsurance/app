package com.hedvig.app.util.extensions

import android.support.design.widget.CollapsingToolbarLayout
import android.widget.TextView

val CollapsingToolbarLayout.titleView: TextView?
    get() {
        for (v in children) {
            if (v is TextView) {
                return v
            }
        }
        return null
    }
