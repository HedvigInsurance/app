package com.hedvig.app.feature.chat

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.graphql.AuthStatusSubscription
import com.hedvig.android.owldroid.graphql.BankIdAuthMutation
import com.hedvig.app.ApolloClientWrapper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class UserViewModel(
    private val userRepository: UserRepository,
    private val apolloClientWrapper: ApolloClientWrapper
) : ViewModel() {

    private val disposables = CompositeDisposable()

    val autoStartToken = MutableLiveData<BankIdAuthMutation.Data>()
    val authStatus = MutableLiveData<AuthStatusSubscription.Data>()

    fun fetchBankIdStartToken() {
        disposables += userRepository
            .subscribeAuthStatus()
            .subscribe({ response ->
                Timber.i("subscribeAuthStatus onNext ${response.data()}")
                authStatus.postValue(response.data())
            }, { e ->
                Timber.i("subscribeAuthStatus onError")
                Timber.e(e)
            }, {
                //TODO: handle in UI
                Timber.i("subscribeAuthStatus was completed")
            })

        disposables += userRepository
            .fetchAutoStartToken()
            .subscribe({ response ->
                autoStartToken.postValue(response.data())
            }, { error ->
                Timber.e(error)
            })
    }

    fun logout(callback: () -> Unit) {
        disposables += userRepository
            .logout()
            .subscribe({
                apolloClientWrapper.invalidateApolloClient()
                callback()
            }, { error ->
                Timber.e(error, "Failed to log out")
            })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
