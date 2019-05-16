package com.hedvig.app.feature.loggedin

import android.arch.lifecycle.ViewModel
import com.hedvig.app.data.chat.ChatRepository
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import javax.inject.Inject

class BaseTabViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val disposables = CompositeDisposable()

    fun triggerFreeTextChat(done: () -> Unit) {
        disposables += chatRepository
            .triggerFreeTextChat()
            .subscribe({ done() }, { Timber.e(it) })
    }
}
