package com.hedvig.app.feature.chat.native

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import com.hedvig.app.util.safeLet
import com.apollographql.apollo.api.Response
import com.hedvig.android.owldroid.graphql.ChatMessageSubscription
import com.hedvig.android.owldroid.graphql.SessionInformationMutation
import com.hedvig.app.feature.chat.UserRepository
import io.reactivex.subscribers.DisposableSubscriber


class UserViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    val newSessionInformation = MutableLiveData<SessionInformationMutation.Data>()

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
