package com.hedvig.app.feature.chat

import android.arch.lifecycle.ViewModel
import com.hedvig.app.ApolloClientWrapper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class UserViewModel(
    private val userRepository: UserRepository,
    private val apolloClientWrapper: ApolloClientWrapper
) : ViewModel() {

    private val disposables = CompositeDisposable()

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
