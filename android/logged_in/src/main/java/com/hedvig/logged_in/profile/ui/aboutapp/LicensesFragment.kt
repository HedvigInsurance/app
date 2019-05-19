package com.hedvig.logged_in.profile.ui.aboutapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.logged_in.R
import com.hedvig.common.util.extensions.compatFont
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_licenses.*
import com.hedvig.app.R as appR

class LicensesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_licenses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = resources.getString(appR.string.PROFILE_LICENSE_ATTRIBUTIONS_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(appR.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(appR.font.circular_bold))
        toolbar.setNavigationIcon(appR.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().findNavController(R.id.loggedInNavigationHost).popBackStack()
        }

        webView.loadUrl("file:///android_asset/open_source_licenses.html")
    }
}
