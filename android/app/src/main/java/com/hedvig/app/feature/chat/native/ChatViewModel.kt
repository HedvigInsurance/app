package com.hedvig.app.feature.chat.native

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class ChatViewModel(
    chatRepository: ChatRepository
) : ViewModel() {

    val messages = MutableLiveData<ChatMessagesQuery.Data>()

    private val disposables = CompositeDisposable()

    init {
        disposables += chatRepository
            .fetchChatMessages()
            .subscribe({ response ->
                val data = response.data()
                messages.postValue(data)
                data?.messages?.filter { m ->
                    true
                }
            }, { Timber.e(it) })
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
