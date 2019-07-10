package com.hedvig.app.feature.chat.native

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import com.hedvig.android.owldroid.graphql.NewSessionMutation
import com.hedvig.app.feature.chat.UserRepository

class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val newSessionInformation = MutableLiveData<NewSessionMutation.Data>()

    private val disposables = CompositeDisposable()

    fun newSession() {
        disposables += userRepository
            .newUserSession()
            .subscribe({ response ->
                newSessionInformation.postValue(response.data())
            }, { error ->
                Timber.e(error, "Failed to create a new session")
            })
    }

    fun logout(callback: () -> Unit) {
        disposables += userRepository
            .logout()
            .subscribe({
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
