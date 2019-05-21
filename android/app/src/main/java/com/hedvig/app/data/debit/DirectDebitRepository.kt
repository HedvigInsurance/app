package com.hedvig.app.data.debit

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.common.owldroid.DirectDebitQuery
import com.hedvig.common.owldroid.type.DirectDebitStatus
import io.reactivex.Observable

class DirectDebitRepository(private val apolloClient: ApolloClient) {
    private lateinit var directDebitQuery: DirectDebitQuery

    fun fetchDirectDebit(): Observable<Response<DirectDebitQuery.Data>> {
        directDebitQuery = DirectDebitQuery
            .builder()
            .build()

        return Rx2Apollo
            .from(apolloClient.query(directDebitQuery).watcher())
    }

    fun refreshDirectdebitStatus(): Observable<Response<DirectDebitQuery.Data>> {
        val bankAccountQuery = DirectDebitQuery
            .builder()
            .build()

        return Rx2Apollo
            .from(
                apolloClient
                    .query(bankAccountQuery)
                    .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
            )
    }

    fun writeDirectDebitStatusToCache(directDebitStatus: DirectDebitStatus) {
        val cachedData = apolloClient
            .apolloStore()
            .read(directDebitQuery)
            .execute()

        val newData = cachedData
            .toBuilder()
            .directDebitStatus(directDebitStatus)
            .build()

        apolloClient
            .apolloStore()
            .writeAndPublish(directDebitQuery, newData)
            .execute()
    }
}
