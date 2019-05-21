package com.hedvig.logged_in.profile.data

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.fetcher.ApolloResponseFetchers
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.common.owldroid.BankAccountQuery
import com.hedvig.common.owldroid.LogoutMutation
import com.hedvig.common.owldroid.ProfileQuery
import com.hedvig.common.owldroid.SelectCashbackMutation
import com.hedvig.common.owldroid.StartDirectDebitRegistrationMutation
import com.hedvig.common.owldroid.UpdateEmailMutation
import com.hedvig.common.owldroid.UpdatePhoneNumberMutation
import io.reactivex.Observable

class ProfileRepository(private val apolloClient: ApolloClient) {
    private lateinit var profileQuery: ProfileQuery
    fun fetchProfile(): Observable<ProfileQuery.Data?> {
        profileQuery = ProfileQuery
            .builder()
            .build()

        return Rx2Apollo
            .from(apolloClient.query(profileQuery).watcher())
            .map { it.data() }
    }

    fun updateEmail(input: String): Observable<Response<UpdateEmailMutation.Data>> {
        val updateEmailMutation = UpdateEmailMutation
            .builder()
            .input(input)
            .build()

        return Rx2Apollo
            .from(apolloClient.mutate(updateEmailMutation))
    }

    fun updatePhoneNumber(input: String): Observable<Response<UpdatePhoneNumberMutation.Data>> {
        val updatePhoneNumberMutation = UpdatePhoneNumberMutation
            .builder()
            .input(input)
            .build()

        return Rx2Apollo
            .from(apolloClient.mutate(updatePhoneNumberMutation))
    }

    fun writeEmailAndPhoneNumberInCache(email: String?, phoneNumber: String?) {
        val cachedData = apolloClient
            .apolloStore()
            .read(profileQuery)
            .execute()
        val newMemberBuilder = cachedData
            .member()
            .toBuilder()

        email?.let { newMemberBuilder.email(it) }
        phoneNumber?.let { newMemberBuilder.phoneNumber(it) }

        val newData = cachedData
            .toBuilder()
            .member(newMemberBuilder.build())
            .build()

        apolloClient
            .apolloStore()
            .writeAndPublish(profileQuery, newData)
            .execute()
    }

    fun selectCashback(id: String): Observable<Response<SelectCashbackMutation.Data>> {
        val selectCashbackMutation = SelectCashbackMutation
            .builder()
            .id(id)
            .build()

        return Rx2Apollo
            .from(apolloClient.mutate(selectCashbackMutation))
    }

    fun writeCashbackToCache(cashback: SelectCashbackMutation.SelectCashbackOption) {
        val cachedData = apolloClient
            .apolloStore()
            .read(profileQuery)
            .execute()

        val newCashback = ProfileQuery.Cashback
            .builder()
            .__typename(cashback.__typename())
            .name(cashback.name())
            .imageUrl(cashback.imageUrl())
            .paragraph(cashback.paragraph())
            .build()

        val newData = cachedData
            .toBuilder()
            .cashback(newCashback)
            .build()

        apolloClient
            .apolloStore()
            .writeAndPublish(profileQuery, newData)
            .execute()
    }

    fun startTrustlySession(): Observable<StartDirectDebitRegistrationMutation.Data> {
        val startDirectDebitRegistrationMutation = StartDirectDebitRegistrationMutation
            .builder()
            .build()

        return Rx2Apollo
            .from(apolloClient.mutate(startDirectDebitRegistrationMutation))
            .map { it.data() }
    }

    fun refreshBankAccountInfo(): Observable<Response<BankAccountQuery.Data>> {
        val bankAccountQuery = BankAccountQuery
            .builder()
            .build()

        return Rx2Apollo
            .from(
                apolloClient
                    .query(bankAccountQuery)
                    .responseFetcher(ApolloResponseFetchers.NETWORK_ONLY)
            )
    }

    fun writeBankAccountInfoToCache(bankAccount: BankAccountQuery.BankAccount) {
        val cachedData = apolloClient
            .apolloStore()
            .read(profileQuery)
            .execute()

        val newBankAccount = ProfileQuery.BankAccount
            .builder()
            .__typename(bankAccount.__typename())
            .bankName(bankAccount.bankName())
            .descriptor(bankAccount.descriptor())
            .build()

        val newData = cachedData
            .toBuilder()
            .bankAccount(newBankAccount)
            .build()

        apolloClient
            .apolloStore()
            .writeAndPublish(profileQuery, newData)
            .execute()
    }

    fun logout() = Rx2Apollo.from(apolloClient.mutate(LogoutMutation()))
}
