package com.hedvig.app.feature.profile.ui.myhome

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.hedvig.app.R
import com.hedvig.app.feature.profile.ui.ProfileViewModel
import com.hedvig.common.owldroid.type.InsuranceType
import com.hedvig.common.util.extensions.compatColor
import com.hedvig.common.util.extensions.compatSetTint
import com.hedvig.app.util.setupLargeTitle
import com.hedvig.common.util.extensions.view.remove
import com.hedvig.common.util.extensions.view.show
import com.hedvig.common.util.interpolateTextKey
import kotlinx.android.synthetic.main.fragment_my_home.*
import kotlinx.android.synthetic.main.loading_spinner.*
import kotlinx.android.synthetic.main.sphere_container.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class MyHomeFragment : Fragment() {
    val profileViewModel: ProfileViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_my_home, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(R.string.PROFILE_MY_HOME_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            requireActivity().findNavController(R.id.rootNavigationHost).popBackStack()
        }
        sphere.drawable.compatSetTint(requireContext().compatColor(R.color.maroon))

        changeHomeInformation.setOnClickListener {
            fragmentManager?.let { fm ->
                val changeHomeInformationDialog =
                    ChangeHomeInfoDialog()
                val transaction = fm.beginTransaction()
                val prev = fm.findFragmentByTag("dialog")
                prev?.let { transaction.remove(it) }
                transaction.addToBackStack(null)
                changeHomeInformationDialog.show(transaction, "dialog")
            }
        }

        loadData()
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()
            sphereContainer.show()

            profileData?.insurance()?.let { insuranceData ->
                sphereText.text = insuranceData.address()
                postalNumber.text = insuranceData.postalNumber()
                insuranceType.text =
                    when (insuranceData.type()) {
                        InsuranceType.BRF -> resources.getString(R.string.PROFILE_MY_HOME_INSURANCE_TYPE_BRF)
                        InsuranceType.STUDENT_BRF -> resources.getString(R.string.PROFILE_MY_HOME_INSURANCE_TYPE_BRF)
                        InsuranceType.RENT -> resources.getString(R.string.PROFILE_MY_HOME_INSURANCE_TYPE_RENT)
                        InsuranceType.STUDENT_RENT -> resources.getString(R.string.PROFILE_MY_HOME_INSURANCE_TYPE_RENT)
                        else -> ""
                    }
                livingSpace.text = interpolateTextKey(
                    resources.getString(R.string.PROFILE_MY_HOME_SQUARE_METER_POSTFIX),
                    "SQUARE_METER" to insuranceData.livingSpace().toString()
                )
                infoContainer.show()
            }
        })
    }
}
