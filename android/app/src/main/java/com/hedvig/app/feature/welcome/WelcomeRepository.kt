package com.hedvig.app.feature.welcome

import com.apollographql.apollo.ApolloClient
import com.hedvig.app.feature.dismissablepager.DismissablePagerPage
import io.reactivex.Observable

class WelcomeRepository(
    private val apolloClient: ApolloClient
) {
    fun fetchWelcomeScreens() =
        Observable.just(listOf(DismissablePagerPage("/app-content-service/referrals_bonus_rain.svg", "TODO", "TODO")))
}
