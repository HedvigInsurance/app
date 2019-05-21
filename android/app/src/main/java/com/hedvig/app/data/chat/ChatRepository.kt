package com.hedvig.app.data.chat

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.common.owldroid.TriggerFreeTextChatMutation
import io.reactivex.Observable

class ChatRepository(private val apolloClient: ApolloClient) {
    fun triggerFreeTextChat(): Observable<Response<TriggerFreeTextChatMutation.Data>> {
        val triggerFreeTextChatMutation = TriggerFreeTextChatMutation.builder().build()

        return Rx2Apollo.from(apolloClient.mutate(triggerFreeTextChatMutation))
    }
}
