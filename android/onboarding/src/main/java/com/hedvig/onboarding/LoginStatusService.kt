package com.hedvig.onboarding

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.app.service.LoginStatus
import com.hedvig.common.owldroid.InsuranceStatusQuery
import com.hedvig.common.owldroid.type.InsuranceStatus
import com.hedvig.common.util.extensions.isLoggedIn
import com.hedvig.common.util.extensions.setIsLoggedIn
import com.hedvig.onboarding.react.AsyncStorageNative
import io.reactivex.Observable

class LoginStatusService(
    private val apolloClient: ApolloClient,
    private val asyncStorageNative: AsyncStorageNative,
    private val context: Context
) {
    fun getLoginStatus(): Observable<LoginStatus> {
        if (context.isLoggedIn()) {
            return Observable.just(LoginStatus.LOGGED_IN)
        }

        val isViewingOffer = asyncStorageNative.getKey("@hedvig:isViewingOffer")

        if (isViewingOffer == "true") {
            return Observable.just(LoginStatus.IN_OFFER)
        }

        asyncStorageNative.getKey("@hedvig:token") ?: return Observable.just(LoginStatus.ONBOARDING)

        return Rx2Apollo.from(apolloClient.query(InsuranceStatusQuery()))
            .map { response ->
                response.data()?.insurance()?.status()?.let { status ->
                    when (status) {
                        InsuranceStatus.ACTIVE,
                        InsuranceStatus.INACTIVE,
                        InsuranceStatus.INACTIVE_WITH_START_DATE -> {
                            context.setIsLoggedIn(true)
                            LoginStatus.LOGGED_IN
                        }
                        InsuranceStatus.PENDING,
                        InsuranceStatus.`$UNKNOWN` -> {
                            context.setIsLoggedIn(false)
                            LoginStatus.ONBOARDING
                        }
                        else -> LoginStatus.ONBOARDING
                    }
                } ?: LoginStatus.ONBOARDING
            }
    }
}
