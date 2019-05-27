package com.hedvig.app.feature.whatsnew

import android.content.Context
import android.support.v4.view.ViewPager
import android.util.AttributeSet
import android.widget.LinearLayout
import timber.log.Timber

class PagerIndicatorView : LinearLayout {
    constructor(context: Context) : super(context)
    constructor(context: Context, attributeSet: AttributeSet?) : super(context, attributeSet)
    constructor(context: Context, attributeSet: AttributeSet?, defStyle: Int) : super(context, attributeSet, defStyle)

    var pager: ViewPager? = null
        set(value) {
            field = value
            value?.addOnPageChangeListener(PageChangeListener())
        }

    inner class PageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun onPageSelected(p0: Int) {
            Timber.e("I was selected: %d", p0)
        }
    }
}
