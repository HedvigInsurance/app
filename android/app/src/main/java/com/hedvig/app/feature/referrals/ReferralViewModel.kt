package com.hedvig.app.feature.referrals

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class ReferralViewModel(
    private val referralRepository: ReferralRepository
) :
    ViewModel() {

    val redeemCodeStatus: MutableLiveData<Boolean> = MutableLiveData()
    private val disposables = CompositeDisposable()

    fun redeemReferralCode(code: String) {
        disposables += referralRepository
            .redeemReferralCode(code)
            .subscribe({
                // TODO Handle the error gracefully
                redeemCodeStatus.postValue(true)
            }, { error ->
                Timber.e(error, "Failed to redeem code")
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
