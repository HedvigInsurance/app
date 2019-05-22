package com.hedvig.app

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCache
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.google.firebase.analytics.FirebaseAnalytics
import com.hedvig.common.owldroid.type.CustomType
import com.hedvig.app.data.debit.DirectDebitRepository
import com.hedvig.app.service.*
import com.hedvig.common.util.apollo.ApolloTimberLogger
import com.hedvig.common.util.apollo.PromiscuousLocalDateAdapter
import com.hedvig.app.viewmodel.DirectDebitViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber
import java.io.File

fun isDebug() = BuildConfig.DEBUG || BuildConfig.APP_ID == "com.hedvig.test.app"

val applicationModule = module {
    single<AsyncStorageNative> { AsyncStorageNativeImpl(get()) }

    single {
        val builder = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val builder = original
                    .newBuilder()
                    .method(original.method(), original.body())
                try {
                    get<AsyncStorageNative>().getKey("@hedvig:token")
                } catch (exception: Exception) {
                    Timber.e(exception, "Got an exception while trying to retrieve token")
                    null
                }?.let { token ->
                    builder.header("Authorization", token)
                }
                chain.proceed(builder.build())
            }
        if (isDebug()) {
            builder.addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                Timber.tag("OkHttp").i(message)
            }).setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        builder.build()
    }
    single { FirebaseAnalytics.getInstance(get()) }

    single<NormalizedCacheFactory<LruNormalizedCache>> {
        LruNormalizedCacheFactory(
            EvictionPolicy.builder().maxSizeBytes(
                1000 * 1024
            ).build()
        )
    }
    single {
        val builder = ApolloClient
            .builder()
            .serverUrl(BuildConfig.GRAPHQL_URL)
            .okHttpClient(get())
            .addCustomTypeAdapter(CustomType.LOCALDATE, PromiscuousLocalDateAdapter())
            .normalizedCache(get())

        if (isDebug()) {
            builder.logger(ApolloTimberLogger())
        }
        builder.build()
    }
}

val viewModelModule = module {
    viewModel { DirectDebitViewModel(get()) }
}

val serviceModule = module {
    single { FileService(get()) }
    single { Referrals(get()) }
    single { RemoteConfig() }
    single { TextKeys(get()) }
    single { LoginStatusService(get(), get(), get()) }
}

val repositoriesModule = module {
    single { com.hedvig.app.data.chat.ChatRepository(get()) }
    single { DirectDebitRepository(get()) }
}
