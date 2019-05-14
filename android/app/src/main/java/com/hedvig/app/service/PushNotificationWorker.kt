package com.hedvig.app.service

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.hedvig.android.owldroid.graphql.NewSessionMutation
import com.hedvig.android.owldroid.graphql.RegisterPushTokenMutation
import com.hedvig.app.util.react.AsyncStorageNative
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class PushNotificationWorker(
    context: Context,
    params: WorkerParameters
) : Worker(context, params), KoinComponent {

    val apolloClient: ApolloClient by inject()
    val asyncStorageNative: AsyncStorageNative by inject()

    private val disposables = CompositeDisposable()

    override fun doWork(): Result {
        val pushToken = inputData.getString(PUSH_TOKEN) ?: throw Exception("No token provided")
        if (!hasHedvigToken()) {
            acquireHedvigToken {
                registerPushToken(pushToken)
            }
            return Result.success()
        }
        registerPushToken(pushToken)
        return Result.success()
    }

    private fun acquireHedvigToken(done: () -> Unit) {
        disposables += Rx2Apollo
            .from(apolloClient.mutate(NewSessionMutation()))
            .subscribe({ response ->
                if (response.hasErrors()) {
                    Timber.e("Failed to register a hedvig token: %s", response.errors().toString())
                    return@subscribe
                }
                response.data()?.createSessionV2()?.token()?.let { hedvigToken ->
                    asyncStorageNative.setKey(HEDVIG_TOKEN, hedvigToken)
                    Timber.i("Successfully saved hedvig token")
                    done()
                } ?: Timber.e("createSession returned no token")
            }, { Timber.e(it) })
    }

    private fun hasHedvigToken(): Boolean {
        try {
            val hedvigToken = asyncStorageNative.getKey(HEDVIG_TOKEN)
            if (hedvigToken != null) {
                return true
            }
        } catch (exception: Exception) {
            Timber.e(exception)
        }
        return false
    }

    private fun registerPushToken(pushToken: String) {
        Timber.i("Registering push token")
        val registerPushTokenMutation = RegisterPushTokenMutation
            .builder()
            .pushToken(pushToken)
            .build()

        disposables += Rx2Apollo
            .from(apolloClient.mutate(registerPushTokenMutation))
            .subscribe({ response ->
                if (response.hasErrors()) {
                    Timber.e("Failed to register push token: %s", response.errors().toString())
                    return@subscribe
                }
                Timber.i("Successfully registered push token")
            }, { Timber.e(it, "Failed to register push token") })
    }

    companion object {
        private const val HEDVIG_TOKEN = "@hedvig:token"
        const val PUSH_TOKEN = "push_token"
    }
}

