package com.hedvig.logged_in.profile.ui.coinsured

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.SpannableString
import android.text.Spanned
import android.text.style.AbsoluteSizeSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.logged_in.R
import com.hedvig.logged_in.profile.ui.ProfileViewModel
import com.hedvig.common.util.CustomTypefaceSpan
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.common.util.extensions.compatFont
import com.hedvig.common.util.extensions.compatSetTint
import com.hedvig.common.util.extensions.concat
import com.hedvig.common.util.extensions.view.remove
import com.hedvig.common.util.extensions.view.show
import com.hedvig.logged_in.util.setupLargeTitle
import kotlinx.android.synthetic.main.fragment_coinsured.*
import kotlinx.android.synthetic.main.loading_spinner.*
import org.koin.android.viewmodel.ext.android.sharedViewModel
import com.hedvig.app.R as appR

class CoinsuredFragment : Fragment() {

    val profileViewModel: ProfileViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_coinsured, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(appR.string.PROFILE_COINSURED_TITLE, appR.font.circular_bold, appR.drawable.ic_back) {
            requireActivity().findNavController(R.id.loggedInNavigationHost).popBackStack()
        }

        coinsuredSphere.drawable.compatSetTint(requireContext().compatColor(appR.color.purple))

        loadData()
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()
            sphereContainer.show()
            textContainer.show()

            loadingAnimation.show()
            loadingAnimation.useHardwareAcceleration(true)
            loadingAnimation.playAnimation()

            profileData?.insurance()?.personsInHousehold()?.let { personsInHousehold ->
                val label = resources.getString(appR.string.PROFILE_COINSURED_QUANTITY_LABEL)
                val partOne = SpannableString("$personsInHousehold\n")
                val partTwo = SpannableString(label)
                partOne.setSpan(
                    CustomTypefaceSpan(requireContext().compatFont(appR.font.soray_extrabold)),
                    0,
                    1,
                    Spanned.SPAN_EXCLUSIVE_INCLUSIVE
                )
                partTwo.setSpan(AbsoluteSizeSpan(16, true), 0, label.length, Spanned.SPAN_EXCLUSIVE_INCLUSIVE)

                sphereText.text = partOne.concat(partTwo)
            }
        })
    }
}
