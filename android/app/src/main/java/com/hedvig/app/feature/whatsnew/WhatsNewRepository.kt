package com.hedvig.app.feature.whatsnew

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.WhatsNewQuery
import com.hedvig.android.owldroid.type.Locale
import com.hedvig.app.BuildConfig

class WhatsNewRepository(
    private val apolloClient: ApolloClient
) {
    fun fetchWhatsNew() =
        Rx2Apollo.from(
            apolloClient.query(
                WhatsNewQuery
                    .builder()
                    .locale(Locale.SV_SE)
                    .sinceVersion(BuildConfig.VERSION_NAME)
                    .build()
            )
        )
}
