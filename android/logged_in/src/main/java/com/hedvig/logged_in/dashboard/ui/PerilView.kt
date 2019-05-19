package com.hedvig.logged_in.dashboard.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.ViewGroup
import android.widget.LinearLayout
import com.hedvig.logged_in.R
import com.hedvig.common.util.extensions.compatDrawable
import kotlinx.android.synthetic.main.peril_view.view.*
import com.hedvig.app.R as appR

class PerilView : LinearLayout {
    private var attributeSet: AttributeSet? = null
    private var defStyle: Int = 0

    private val iconSize: Int by lazy { resources.getDimensionPixelSize(appR.dimen.peril_icon) }

    private val doubleMargin: Int by lazy { resources.getDimensionPixelSize(appR.dimen.base_margin_double) }

    constructor(context: Context) : super(context) {
        inflate(context, R.layout.peril_view, this)
        setupAttributes()
    }

    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet) {
        inflate(context, R.layout.peril_view, this)
        this.attributeSet = attributeSet
        setupAttributes()
    }

    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle) {
        inflate(context, R.layout.peril_view, this)
        this.attributeSet = attributeSet
        this.defStyle = defStyle
        setupAttributes()
    }

    var perilIcon: Drawable? = null
        set(value) {
            field = value
            image.setImageDrawable(value)
        }

    var perilIconId: String? = null
        set(value) {
            field = value
            value?.let {
                image.setImageDrawable(
                    context.compatDrawable(
                        PerilIcon.from(
                            it
                        )
                    )
                )
            }
        }

    var perilName: CharSequence? = null
        set(value) {
            field = value
            text.text = value
        }

    fun setupAttributes() {
        orientation = VERTICAL

        val attributes = context.obtainStyledAttributes(
            attributeSet,
            appR.styleable.PerilView,
            defStyle,
            0
        )

        perilIcon = attributes.getDrawable(appR.styleable.PerilView_perilIcon)
        perilName = attributes.getText(appR.styleable.PerilView_perilText)

        attributes.recycle()
    }

    companion object {
        fun build(
            context: Context,
            width: Int = ViewGroup.LayoutParams.WRAP_CONTENT,
            height: Int = ViewGroup.LayoutParams.WRAP_CONTENT
        ) = PerilView(context).apply {
            layoutParams = MarginLayoutParams(width, height).also { lp ->
                lp.topMargin = doubleMargin
                lp.marginStart = doubleMargin
                lp.marginEnd = doubleMargin
            }
        }
    }
}
