package com.hedvig.app.feature.whatsnew

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.hedvig.android.owldroid.graphql.WhatsNewQuery
import com.hedvig.app.BuildConfig
import com.hedvig.app.R
import com.hedvig.app.util.extensions.observe
import com.hedvig.app.util.extensions.screenWidth
import com.hedvig.app.util.extensions.view.setHapticClickListener
import kotlinx.android.synthetic.main.fragment_whats_new.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class WhatsNewDialog : DialogFragment() {

    private val whatsNewViewModel: WhatsNewViewModel by sharedViewModel()
    private val tracker: WhatsNewTracker by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.FullScreenDialog)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        dialog?.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_whats_new, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        whatsNewViewModel.fetchNews(arguments?.getString(SINCE_VERSION))

        close.setHapticClickListener {
            dialog?.dismiss()
        }
        whatsNewViewModel.news.observe(this) { data ->
            data?.let { bindData(it) }
        }
    }

    private fun bindData(data: WhatsNewQuery.Data) {
        pager.adapter = PagerAdapter(childFragmentManager, data.news)
        pagerIndicator.pager = pager
        proceed.text = if (data.news.size > 1) {
            resources.getString(R.string.NEWS_PROCEED)
        } else {
            resources.getString(R.string.NEWS_DISMISS)
        }
        pager.addOnPageChangeListener(PageChangeListener())
        proceed.setHapticClickListener {
            tracker.trackClickProceed()
            pager.currentItem += 1
        }
    }

    inner class PageChangeListener : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(p0: Int) {}

        override fun onPageScrolled(position: Int, offsetPercentage: Float, offsetPixels: Int) {
            pager.adapter?.count?.let { count ->
                if (position == count - 2) {
                    newsContainer.alpha = 1.0f - offsetPercentage
                    val translation = -(screenWidth * offsetPercentage)
                    proceed.translationX = translation
                    topBar.translationX = translation
                    pagerIndicator.translationX = translation
                }
                if (position == count - 1 && offsetPercentage == 0f) {
                    whatsNewViewModel.hasSeenNews(BuildConfig.VERSION_NAME)
                    dialog?.dismiss()
                }
            }
        }

        override fun onPageSelected(page: Int) {
            pager.adapter?.count?.let { count ->
                proceed.text = if (isPositionLast(page, count) || isPositionNextToLast(page, count)) {
                    resources.getString(R.string.NEWS_DISMISS)
                } else {
                    resources.getString(R.string.NEWS_PROCEED)
                }
            }
        }
    }

    companion object {
        const val TAG = "whats_new_dialog"

        private const val SINCE_VERSION = "since_version"

        fun newInstance(sinceVersion: String? = null) = WhatsNewDialog().apply {
            sinceVersion?.let { sv ->
                arguments = Bundle().apply {
                    putString(SINCE_VERSION, sv)
                }
            }
        }
    }
}
