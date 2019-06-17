package com.hedvig.app.feature.whatsnew

import android.graphics.drawable.PictureDrawable
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.RequestBuilder
import com.hedvig.app.BuildConfig
import com.hedvig.app.R
import com.hedvig.app.util.svg.buildRequestBuilder
import kotlinx.android.synthetic.main.fragment_news.*

class NewsFragment : Fragment() {
    private val requestBuilder: RequestBuilder<PictureDrawable> by lazy { buildRequestBuilder() }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_news, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getString(NEWS_ILLUSTRATION)?.let { il ->
            requestBuilder
                .load(Uri.parse(BuildConfig.BASE_URL + il))
                .into(illustration)
        }
        title.text = arguments?.getString(NEWS_TITLE)
        paragraph.text = arguments?.getString(NEWS_PARAGRAPH)
    }

    companion object {
        fun newInstance(illustration: String, title: String, paragraph: String): NewsFragment {
            val fragment = NewsFragment()

            val arguments = Bundle().apply {
                putString(NEWS_ILLUSTRATION, illustration)
                putString(NEWS_TITLE, title)
                putString(NEWS_PARAGRAPH, paragraph)
            }
            fragment.arguments = arguments

            return fragment
        }

        private const val NEWS_ILLUSTRATION = "NEWS_ILLUSTRATION"
        private const val NEWS_TITLE = "NEWS_TITLE"
        private const val NEWS_PARAGRAPH = "NEWS_PARAGRAPH"
    }
}
