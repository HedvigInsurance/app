package com.hedvig.app.feature.whatsnew

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import com.hedvig.android.owldroid.graphql.WhatsNewQuery
import com.hedvig.app.R
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.view.setHapticClickListener
import kotlinx.android.synthetic.main.fragment_whats_new.*
import org.koin.android.viewmodel.ext.android.viewModel

class WhatsNewActivity : AppCompatActivity() {

    private val whatsNewViewModel: WhatsNewViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_whats_new)


        whatsNewViewModel.news.observe(this) { data ->
            data?.let { bindData(it) }
        }
    }

    private fun bindData(data: WhatsNewQuery.Data) {
        pager.adapter = PagerAdapter(supportFragmentManager, data.news)
        pagerIndicator.pager = pager
        proceed.text = if (data.news.size > 1) {
            resources.getString(R.string.NEWS_PROCEED)
        } else {
            resources.getString(R.string.NEWS_DISMISS)
        }
        pager.addOnPageChangeListener(PageChangeListener())
        proceed.setHapticClickListener {
            pager.currentItem += 1
        }
    }

    inner class PageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}

        override fun onPageScrolled(position: Int, offsetPercentage: Float, offsetPixels: Int) {
            pager.adapter?.count?.let { count ->
                if (position == count - 2) {
                    proceed.alpha = Math.max(0f, 1.0f - (offsetPercentage * 2))
                    newsContainer.alpha = 1.0f - offsetPercentage
                }
            }
        }

        override fun onPageSelected(page: Int) {
            pager.adapter?.count?.let { count ->
                proceed.text = if (page == count - 2 || page == count - 1) {
                    resources.getString(R.string.NEWS_DISMISS)
                } else {
                    resources.getString(R.string.NEWS_PROCEED)
                }
            }
        }
    }
}
