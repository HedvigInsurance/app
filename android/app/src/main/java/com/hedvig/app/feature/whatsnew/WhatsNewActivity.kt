package com.hedvig.app.feature.whatsnew

import android.app.Activity
import android.os.Bundle
import com.hedvig.app.R
import kotlinx.android.synthetic.main.fragment_whats_new.*

class WhatsNewActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_whats_new)

        pagerIndicator.pager = pager
    }
}
