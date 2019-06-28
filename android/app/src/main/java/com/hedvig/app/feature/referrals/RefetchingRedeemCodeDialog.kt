package com.hedvig.app.feature.referrals

import com.hedvig.app.feature.profile.ui.ProfileViewModel
import org.koin.android.viewmodel.ext.android.sharedViewModel

class RefetchingRedeemCodeDialog : RedeemCodeDialog() {
    private val profileViewModel: ProfileViewModel by sharedViewModel()

    override fun onRedeemSuccess() {
        profileViewModel.updateReferralsInformation()
    }
}
