package com.hedvig.app.feature.referrals

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.graphql.RedeemReferralCodeMutation
import com.hedvig.android.owldroid.graphql.ReferralCampaignMemberInformationQuery
import com.hedvig.android.owldroid.type.RedeemCodeStatus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class ReferralViewModel(
    private val referralRepository: ReferralRepository
) :
    ViewModel() {

    val redeemCodeStatus: MutableLiveData<RedeemCodeStatus> = MutableLiveData()
    val referralCampaignMemberInformation: MutableLiveData<ReferralCampaignMemberInformationQuery.ReferralCampaignMemberInformation> = MutableLiveData()

    private val disposables = CompositeDisposable()

    fun fetchReferralCampaignMemberInformation(code: String) {
        disposables += referralRepository
            .fetchReferralCampaignMemberInformation(code)
            .subscribe({
                it.data()?.referralCampaignMemberInformation?.let { data ->
                    referralCampaignMemberInformation.postValue(data)
                }
            }, { error ->
                Timber.e(error, "Failed to redeem code")
            })
    }

    fun redeemReferralCode(code: String) {
        disposables += referralRepository
            .redeemReferralCode(code)
            .subscribe({
                it.data()?.redeemCode?.let { status ->
                    redeemCodeStatus.postValue(status)
                }
            }, { error ->
                Timber.e(error, "Failed to redeem code")
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
