package com.hedvig.app.feature.chat.native

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import java.lang.RuntimeException

class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val messages = MutableLiveData<ChatMessagesQuery.Data>()
    val re = MutableLiveData<Boolean>()

    private val disposables = CompositeDisposable()

    init {
        disposables += chatRepository
            .fetchChatMessages()
            .subscribe({ messages.postValue(it.data()) }, { Timber.e(it) })
    }

    fun respondToLasMessage(message: String) {
        val id = messages.value?.messages?.firstOrNull()?.globalId
            ?: throw RuntimeException("Messages is not initialized!")

        disposables += chatRepository
            .sendChatMessage(id, message)
            .subscribe({ re.postValue(it.data()?.isSendChatTextResponse) }, { Timber.e(it) })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
