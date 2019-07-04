package com.hedvig.app.feature.chat.native

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import com.hedvig.app.util.extensions.mapToMessage
import com.hedvig.app.util.safeLet
import com.apollographql.apollo.api.Response
import com.hedvig.android.owldroid.graphql.ChatMessageSubscription
import io.reactivex.subscribers.DisposableSubscriber


class ChatViewModel(
    private val chatRepository: ChatRepository
) : ViewModel() {

    val messages = MutableLiveData<ChatMessagesQuery.Data>()
    val sendMessageResponse = MutableLiveData<Boolean>()

    private val disposables = CompositeDisposable()

    init {
        disposables += chatRepository
            .fetchChatMessages()
            .subscribe({ messages.postValue(it.data()) }, { Timber.e(it) })

        disposables += Rx2Apollo.from(chatRepository.subscribeToChatMessages())
            .subscribeWith(
                object : DisposableSubscriber<Response<ChatMessageSubscription.Data>>() {
                    override fun onNext(response: Response<ChatMessageSubscription.Data>) {
                        Timber.e("onNext")
                        response.data()?.message?.let { chatRepository.writeNewMessage(it.mapToMessage()) }
                    }

                    override fun onError(e: Throwable) {
                        Timber.e(e)
                    }

                    override fun onComplete() {
                        Timber.i("subscribeToChatMessages was completed")
                    }
                }
            )
    }


    fun respondToLasMessage(message: String) {
        val id = messages.value?.messages?.firstOrNull()?.globalId
            ?: run {
                Timber.e("Messages is not initialized!")
                return
            }

        disposables += chatRepository
            .sendChatMessage(id, message)
            .subscribe({ sendMessageResponse.postValue(it.data()?.isSendChatTextResponse) }, { Timber.e(it) })
    }


    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}
