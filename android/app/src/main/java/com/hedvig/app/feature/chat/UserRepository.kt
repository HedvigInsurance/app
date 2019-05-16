package com.hedvig.app.feature.chat

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.LogoutMutation
import javax.inject.Inject

class UserRepository(private val apolloClient: ApolloClient) {

    fun logout() = Rx2Apollo.from(apolloClient.mutate(LogoutMutation()))
}
