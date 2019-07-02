package com.hedvig.app.feature.welcome

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import io.reactivex.Observable
import com.hedvig.android.owldroid.graphql.WelcomeQuery
import com.hedvig.android.owldroid.type.Locale

class WelcomeRepository(
    private val apolloClient: ApolloClient
) {
    fun fetchWelcomeScreens(): Observable<WelcomeQuery.Data?> {
        val welcomeQuery = WelcomeQuery
            .builder()
            .locale(Locale.SV_SE)
            .build()

        return Rx2Apollo
            .from(apolloClient.query(welcomeQuery))
            .map { it.data() }
    }
}
