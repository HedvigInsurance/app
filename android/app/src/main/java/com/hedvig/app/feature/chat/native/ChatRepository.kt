package com.hedvig.app.feature.chat.native

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.ApolloSubscriptionCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.ChatMessageSubscription
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.android.owldroid.graphql.SendChatTextResponseMutation
import com.hedvig.android.owldroid.type.ChatResponseBodyTextInput
import com.hedvig.android.owldroid.type.ChatResponseTextInput
import io.reactivex.Flowable
import io.reactivex.Observable
import org.jetbrains.annotations.NotNull

class ChatRepository(
    private val apolloClient: ApolloClient
) {
    private lateinit var messagesQuery: ChatMessagesQuery

    fun fetchChatMessages(): Observable<Response<ChatMessagesQuery.Data>> {
        messagesQuery = ChatMessagesQuery()
        return Rx2Apollo.from(
            apolloClient
                .query(messagesQuery)
                .watcher()
        )
    }

    fun subscribeToChatMessages() =
        Rx2Apollo.from(apolloClient.subscribe(ChatMessageSubscription.builder().build()))

    fun sendChatMessage(id: String, message: String): Observable<Response<SendChatTextResponseMutation.Data>> {
        val input = ChatResponseTextInput.builder()
            .globalId(id)
            .body(ChatResponseBodyTextInput.builder().text(message).build())
            .build()

        val sendChatMessageMutation =
            SendChatTextResponseMutation.builder()
                .input(input)
                .build()

        return Rx2Apollo.from(
            apolloClient.mutate(sendChatMessageMutation))
    }

    fun writeNewMessage(mapToMessage: ChatMessagesQuery.Message) {
        val cachedData = apolloClient
            .apolloStore()
            .read(messagesQuery)
            .execute()

        val newMessagesBuilder = cachedData
            .toBuilder()
            .messages { it.add(0, mapToMessage.toBuilder()) }

        apolloClient
            .apolloStore()
            .writeAndPublish(messagesQuery, newMessagesBuilder.build())
            .execute()
    }
}
