package com.hedvig.app.feature.chat

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.LogoutMutation
import com.hedvig.android.owldroid.graphql.NewSessionMutation
import com.hedvig.app.ApolloClientWrapper

class UserRepository(private val apolloClientWrapper: ApolloClientWrapper) {

    fun newUserSession() = Rx2Apollo.from(apolloClientWrapper.apolloClient.mutate(NewSessionMutation()))

    fun logout() = Rx2Apollo.from(apolloClientWrapper.apolloClient.mutate(LogoutMutation()))
}
