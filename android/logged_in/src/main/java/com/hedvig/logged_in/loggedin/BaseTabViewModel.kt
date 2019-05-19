package com.hedvig.logged_in.loggedin

import android.arch.lifecycle.ViewModel
import com.hedvig.app.data.chat.ChatRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class BaseTabViewModel constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val disposables = CompositeDisposable()

    fun triggerFreeTextChat(done: () -> Unit) {
        disposables += chatRepository
            .triggerFreeTextChat()
            .subscribe({ done() }, { Timber.e(it) })
    }
}
