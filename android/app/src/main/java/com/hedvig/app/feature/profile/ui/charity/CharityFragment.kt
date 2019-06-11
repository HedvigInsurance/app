package com.hedvig.app.feature.profile.ui.charity

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.hedvig.android.owldroid.graphql.ProfileQuery
import com.hedvig.app.R
import com.hedvig.app.feature.profile.service.ProfileTracker
import com.hedvig.app.feature.profile.ui.ProfileViewModel
import com.hedvig.app.util.extensions.setupLargeTitle
import com.hedvig.app.util.extensions.showBottomSheetDialog
import com.hedvig.app.util.extensions.view.remove
import com.hedvig.app.util.extensions.view.setHapticClickListener
import com.hedvig.app.util.extensions.view.show
import kotlinx.android.synthetic.main.fragment_charity.*
import kotlinx.android.synthetic.main.loading_spinner.*
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.sharedViewModel

class CharityFragment : Fragment() {
    val tracker: ProfileTracker by inject()

    val profileViewModel: ProfileViewModel by sharedViewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_charity, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupLargeTitle(R.string.PROFILE_CHARITY_TITLE, R.font.circular_bold, R.drawable.ic_back) {
            requireActivity().findNavController(R.id.loggedInFragment).popBackStack()
        }

        loadData()
    }

    private fun loadData() {
        profileViewModel.data.observe(this, Observer { profileData ->
            loadingSpinner.remove()

            profileData?.let { data ->
                data.cashback?.let { showSelectedCharity(it) } ?: showCharityPicker(data.cashbackOptions)
            }
        })
    }

    private fun showSelectedCharity(cashback: ProfileQuery.Cashback) {
        selectedCharityContainer.show()
        selectCharityContainer.remove()

        Glide
            .with(requireContext())
            .load(cashback.imageUrl)
            .apply(
                RequestOptions().override(
                    Target.SIZE_ORIGINAL,
                    CASH_BACK_IMAGE_HEIGHT
                )
            )
            .into(selectedCharityBanner)

        selectedCharityCardTitle.text = cashback.name
        selectedCharityCardParagraph.text = cashback.paragraph
        charitySelectedHowDoesItWorkButton.setHapticClickListener {
            tracker.howDoesItWorkClick()
            requireFragmentManager().showBottomSheetDialog(R.layout.bottom_sheet_charity_explanation)
        }
    }

    private fun showCharityPicker(options: List<ProfileQuery.CashbackOption>) {
        selectCharityContainer.show()
        cashbackOptions.layoutManager = LinearLayoutManager(requireContext())
        cashbackOptions.adapter =
            CharityAdapter(options, requireContext()) { id ->
                profileViewModel.selectCashback(id)
            }
        selectCharityHowDoesItWorkButton.setHapticClickListener {
            tracker.howDoesItWorkClick()
            requireFragmentManager().showBottomSheetDialog(R.layout.bottom_sheet_charity_explanation)
        }
    }

    companion object {
        private const val CASH_BACK_IMAGE_HEIGHT = 200
    }
}
