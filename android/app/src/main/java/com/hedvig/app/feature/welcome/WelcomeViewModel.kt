package com.hedvig.app.feature.welcome

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.app.feature.dismissablepager.DismissablePagerPage
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class WelcomeViewModel(
    private val welcomeRepository: WelcomeRepository
) : ViewModel() {

    val data = MutableLiveData<List<DismissablePagerPage>>()

    private val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }

    fun fetch() {
        disposables += welcomeRepository
            .fetchWelcomeScreens()
            .subscribe({ response ->
                response.let { data.postValue(it) }
            }, { Timber.e(it) })
    }
}
