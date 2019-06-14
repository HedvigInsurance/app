package com.hedvig.app.feature.profile.ui.aboutapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.hedvig.app.R
import com.hedvig.app.util.extensions.setupLargeTitle
import kotlinx.android.synthetic.main.fragment_licenses.*

class LicensesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_licenses)

        setupLargeTitle(R.string.PROFILE_LICENSE_ATTRIBUTIONS_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            onBackPressed()
        }
        webView.loadUrl("file:///android_asset/open_source_licenses.html")
    }
}
