package com.hedvig.app.feature.profile.ui.aboutapp

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.app.R
import com.hedvig.app.util.extensions.compatFont
import kotlinx.android.synthetic.main.app_bar.*
import kotlinx.android.synthetic.main.fragment_licenses.*

class LicensesFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_licenses, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        collapsingToolbar.title = resources.getString(R.string.PROFILE_LICENSE_ATTRIBUTIONS_TITLE)
        collapsingToolbar.setExpandedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        collapsingToolbar.setCollapsedTitleTypeface(requireContext().compatFont(R.font.circular_bold))
        toolbar.setNavigationIcon(R.drawable.ic_back)
        toolbar.setNavigationOnClickListener {
            requireActivity().findNavController(R.id.loggedNavigationHost).popBackStack()
        }

        webView.loadUrl("file:///android_asset/open_source_licenses.html")
    }
}
