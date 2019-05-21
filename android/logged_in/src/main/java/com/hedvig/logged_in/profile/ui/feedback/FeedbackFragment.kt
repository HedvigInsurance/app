package com.hedvig.logged_in.profile.ui.feedback

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.logged_in.R
import com.hedvig.common.util.whenApiVersion
import com.hedvig.logged_in.util.setupLargeTitle
import kotlinx.android.synthetic.main.fragment_feedback.*
import com.hedvig.app.R as appR

class FeedbackFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_feedback, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(appR.string.PROFILE_FEEDBACK_TITLE, appR.font.circular_bold, appR.drawable.ic_back) {
            requireActivity().findNavController(R.id.loggedInNavigationHost).popBackStack()
        }

        bugReportEmail.setOnClickListener {
            startActivity(Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:${requireContext().getString(appR.string.bug_report_mail)}?subject=Buggrapport")
            })
        }

        playLink.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${requireContext().packageName}"))
            var flags = Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK
            whenApiVersion(Build.VERSION_CODES.LOLLIPOP) {
                flags = flags or Intent.FLAG_ACTIVITY_NEW_DOCUMENT
            }
            startActivity(intent)
        }
    }
}