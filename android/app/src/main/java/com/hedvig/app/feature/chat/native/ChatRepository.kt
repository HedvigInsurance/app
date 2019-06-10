package com.hedvig.app.feature.chat.native

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
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
}
