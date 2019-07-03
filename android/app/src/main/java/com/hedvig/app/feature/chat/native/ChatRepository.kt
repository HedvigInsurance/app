package com.hedvig.app.feature.chat.native

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.android.owldroid.graphql.SendChatTextResponseMutation
import com.hedvig.android.owldroid.type.ChatResponseBodyTextInput
import com.hedvig.android.owldroid.type.ChatResponseTextInput
import com.hedvig.app.feature.chat.ChatTextInput
import io.reactivex.Observable

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
}
