package com.hedvig.app.feature.offer

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.OfferQuery
import io.reactivex.Observable

class OfferRepository(
    private val apolloClient: ApolloClient
) {
    private lateinit var offerQuery: OfferQuery


    fun loadOffer(): Observable<Response<OfferQuery.Data>> {
        offerQuery = OfferQuery()

        return Rx2Apollo
            .from(apolloClient.query(offerQuery))
    }
}
