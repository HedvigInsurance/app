package com.hedvig.app.feature.chat

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.LogoutMutation
import com.hedvig.android.owldroid.graphql.NewSessionMutation

class UserRepository(private val apolloClient: ApolloClient) {

    fun newUserSession() = Rx2Apollo.from(apolloClient.mutate(NewSessionMutation()))

    fun logout() = Rx2Apollo.from(apolloClient.mutate(LogoutMutation()))
}
