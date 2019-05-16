package com.hedvig.app.data.chat

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.TriggerFreeTextChatMutation
import io.reactivex.Observable
import javax.inject.Inject

class ChatRepository(private val apolloClient: ApolloClient) {
    fun triggerFreeTextChat(): Observable<Response<TriggerFreeTextChatMutation.Data>> {
        val triggerFreeTextChatMutation = TriggerFreeTextChatMutation.builder().build()

        return Rx2Apollo.from(apolloClient.mutate(triggerFreeTextChatMutation))
    }
}
