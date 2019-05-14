package com.hedvig.app.feature.profile.ui.aboutapp

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.hedvig.app.BuildConfig
import com.hedvig.app.R
import com.hedvig.app.feature.profile.ui.ProfileViewModel
import com.hedvig.app.util.extensions.proxyNavigate
import com.hedvig.app.util.extensions.setupLargeTitle
import com.hedvig.app.util.interpolateTextKey
import kotlinx.android.synthetic.main.fragment_about_app.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class AboutAppFragment : Fragment() {

    val profileViewModel: ProfileViewModel by sharedViewModel()

    private val navController: NavController by lazy {
        requireActivity().findNavController(R.id.rootNavigationHost)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_about_app, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(R.string.PROFILE_ABOUT_APP_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            navController.popBackStack()
        }

        licenseAttributions.setOnClickListener {
            navController.proxyNavigate(R.id.action_aboutAppFragment_to_licensesFragment)
        }

        versionNumber.text = interpolateTextKey(
            resources.getString(R.string.PROFILE_ABOUT_APP_VERSION),
            "VERSION_NUMBER" to BuildConfig.VERSION_NAME
        )

        profileViewModel.data.observe(this, Observer { data ->
            data?.member()?.id()?.let { id ->
                memberId.text = interpolateTextKey(
                    resources.getString(R.string.PROFILE_ABOUT_APP_MEMBER_ID),
                    "MEMBER_ID" to id
                )
            }
        })
    }
}
