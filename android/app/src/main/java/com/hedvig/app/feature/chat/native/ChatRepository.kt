package com.hedvig.app.feature.chat.native

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.FileUpload
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.api.cache.http.HttpCachePolicy
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.fragment.ChatMessageFragment
import com.hedvig.android.owldroid.graphql.ChatMessageSubscription
import com.hedvig.android.owldroid.graphql.ChatMessagesQuery
import com.hedvig.android.owldroid.graphql.EditLastResponseMutation
import com.hedvig.android.owldroid.graphql.SendChatSingleSelectResponseMutation
import com.hedvig.android.owldroid.graphql.SendChatTextResponseMutation
import com.hedvig.android.owldroid.graphql.UploadClaimMutation
import com.hedvig.android.owldroid.type.ChatResponseBodySingleSelectInput
import com.hedvig.android.owldroid.type.ChatResponseBodyTextInput
import com.hedvig.android.owldroid.type.ChatResponseSingleSelectInput
import com.hedvig.android.owldroid.type.ChatResponseTextInput
import com.hedvig.app.service.FileService
import io.reactivex.Observable
import java.io.File

class ChatRepository(
    private val apolloClient: ApolloClient,
    private val fileService: FileService
) {
    private lateinit var messagesQuery: ChatMessagesQuery

    fun fetchChatMessages(): Observable<Response<ChatMessagesQuery.Data>> {
        messagesQuery = ChatMessagesQuery()
        return Rx2Apollo.from(
            apolloClient
                .query(messagesQuery)
                .httpCachePolicy(HttpCachePolicy.NETWORK_ONLY)
                .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
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
            apolloClient.mutate(sendChatMessageMutation)
        )
    }

    fun sendSingleSelect(id: String, value: String): Observable<Response<SendChatSingleSelectResponseMutation.Data>> {
        val input = ChatResponseSingleSelectInput.builder()
            .globalId(id)
            .body(
                ChatResponseBodySingleSelectInput
                    .builder()
                    .selectedValue(value)
                    .build()
            )
            .build()

        val sendChatSingleSelectMutation = SendChatSingleSelectResponseMutation
            .builder()
            .input(input)
            .build()

        return Rx2Apollo.from(
            apolloClient.mutate(sendChatSingleSelectMutation)
        )
    }

    fun uploadClaim(id: String, path: String): Observable<Response<UploadClaimMutation.Data>> {
        val mutation = UploadClaimMutation.builder()
            .id(id)
            .claim(FileUpload(fileService.getMimeType(path), File(path)))
            .build()

        return Rx2Apollo.from(apolloClient.mutate(mutation))
    }

    fun writeNewMessage(message: ChatMessageFragment) {
        val cachedData = apolloClient
            .apolloStore()
            .read(messagesQuery)
            .execute()

        val chatMessagesFragment =
            ChatMessagesQuery
                .Message
                .Fragments.builder().chatMessageFragment(message).build()

        val chatMessageQueryBuilder =
            ChatMessagesQuery
                .Message
                .builder()
                .__typename(message.__typename)
                .fragments(
                    chatMessagesFragment
                )

        val newMessagesBuilder = cachedData
            .toBuilder()
            .messages { it.add(0, chatMessageQueryBuilder) }

        apolloClient
            .apolloStore()
            .writeAndPublish(messagesQuery, newMessagesBuilder.build())
            .execute()
    }

    fun editLastResponse() = Rx2Apollo.from(apolloClient.mutate(EditLastResponseMutation()))
}
