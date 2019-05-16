package com.hedvig.app.feature.dashboard.data

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.DashboardQuery
import io.reactivex.Observable

class DashboardRepository(
    val apolloClient: ApolloClient
) {
    fun fetchDashboard(): Observable<Response<DashboardQuery.Data>> {
        val dashboardQuery = DashboardQuery.builder().build()

        return Rx2Apollo.from(apolloClient.query(dashboardQuery))
    }
}
