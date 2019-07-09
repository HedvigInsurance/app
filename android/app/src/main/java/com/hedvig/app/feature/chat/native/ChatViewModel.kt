package com.hedvig.app.feature.chat.native

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber


class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val messages = MutableLiveData<ChatMessagesQuery.Data>()
    val sendMessageResponse = MutableLiveData<Boolean>()
    val sendSingelSelectResponse = MutableLiveData<Boolean>()

    private val disposables = CompositeDisposable()

    fun loadAndSubscribe() {
        load()

        disposables += chatRepository.subscribeToChatMessages()
            .subscribe({ response ->
                Timber.e("onNext")
                response.data()?.message?.let {
                    chatRepository
                        .writeNewMessage(
                            it.fragments.chatMessageFragment
                        )
                }
            }, {
                Timber.e(it)
            }, {
                //TODO: handle in UI
                Timber.i("subscribeToChatMessages was completed")
            })
    }

    fun load() {
        disposables += chatRepository
            .fetchChatMessages()
            .subscribe( { response ->
                val data = response.data()
                messages.postValue(data)
                //TODO: look at this
                data?.messages?.filter {
                    true
                }
            }, { Timber.e(it) })
    }

    fun respondToLastMessage(message: String) {
        disposables += chatRepository
            .sendChatMessage(getLastId(), message)
            .subscribe({ sendMessageResponse.postValue(it.data()?.isSendChatTextResponse) }, { Timber.e(it) })
    }

    fun respondWithSingleSelect(value: String) {
        disposables += chatRepository
            .sendSingleSelect(getLastId(), value)
            .subscribe({ sendSingelSelectResponse.postValue(it.data()?.isSendChatSingleSelectResponse) }, { Timber.e(it) })
    }

    private fun getLastId(): String =
        messages.value?.messages?.firstOrNull()?.fragments?.chatMessageFragment?.globalId
            ?: throw RuntimeException("Messages is not initialized!")

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
