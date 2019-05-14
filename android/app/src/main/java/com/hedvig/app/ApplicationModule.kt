package com.hedvig.app

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.Logger
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCache
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.firebase.analytics.FirebaseAnalytics
import com.hedvig.android.owldroid.type.CustomType
import com.hedvig.app.data.debit.DirectDebitRepository
import com.hedvig.app.feature.chat.ChatRepository
import com.hedvig.app.feature.chat.ChatViewModel
import com.hedvig.app.feature.chat.UserRepository
import com.hedvig.app.feature.claims.data.ClaimsRepository
import com.hedvig.app.feature.claims.service.ClaimsTracker
import com.hedvig.app.feature.claims.ui.ClaimsViewModel
import com.hedvig.app.feature.dashboard.data.DashboardRepository
import com.hedvig.app.feature.dashboard.service.DashboardTracker
import com.hedvig.app.feature.dashboard.ui.DashboardViewModel
import com.hedvig.app.feature.loggedin.BaseTabViewModel
import com.hedvig.app.feature.marketing.data.MarketingStoriesRepository
import com.hedvig.app.feature.marketing.service.MarketingTracker
import com.hedvig.app.feature.marketing.ui.MarketingStoriesViewModel
import com.hedvig.app.feature.profile.data.ProfileRepository
import com.hedvig.app.feature.profile.service.ProfileTracker
import com.hedvig.app.feature.profile.ui.ProfileViewModel
import com.hedvig.app.service.FileService
import com.hedvig.app.service.LoginStatusService
import com.hedvig.app.service.Referrals
import com.hedvig.app.service.RemoteConfig
import com.hedvig.app.service.TextKeys
import com.hedvig.app.util.apollo.ApolloTimberLogger
import com.hedvig.app.util.apollo.PromiscuousLocalDateAdapter
import com.hedvig.app.util.react.AsyncStorageNative
import com.hedvig.app.util.react.AsyncStorageNativeImpl
import com.hedvig.app.viewmodel.DirectDebitViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import timber.log.Timber
import java.io.File

fun isDebug() = BuildConfig.DEBUG || BuildConfig.APP_ID == "com.hedvig.test.app"

val applicationModule = module {
    single { FirebaseAnalytics.getInstance(get()) }
    single {
        SimpleCache(
            File(get<Context>().cacheDir, "hedvig_story_video_cache"),
            LeastRecentlyUsedCacheEvictor((10 * 1024 * 1024).toLong())
        )
    }
    single<AsyncStorageNative> { AsyncStorageNativeImpl(get()) }
    single<Logger?> { if (isDebug()) ApolloTimberLogger() else null }
    single {
        if (isDebug()) {
            HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
                Timber.tag("OkHttp").i(message)
            }).setLevel(HttpLoggingInterceptor.Level.BODY)
        } else null
    }
    single<NormalizedCacheFactory<LruNormalizedCache>> {
        LruNormalizedCacheFactory(
            EvictionPolicy.builder().maxSizeBytes(
                1000 * 1024
            ).build()
        )
    }
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
        get<HttpLoggingInterceptor?>()?.let { builder.addInterceptor(it) }
        builder.build()
    }
    single {
        val builder = ApolloClient
            .builder()
            .serverUrl(BuildConfig.GRAPHQL_URL)
            .okHttpClient(get())
            .addCustomTypeAdapter(CustomType.LOCALDATE, PromiscuousLocalDateAdapter())
            .normalizedCache(get())

        get<Logger?>().let { builder.logger(it) }
        builder.build()
    }
}

val viewModelModule = module {
    viewModel { MarketingStoriesViewModel(get()) }
    viewModel { ProfileViewModel(get(), get(), get(), get()) }
    viewModel { ClaimsViewModel(get(), get()) }
    viewModel { DirectDebitViewModel(get()) }
    viewModel { DashboardViewModel(get(), get()) }
    viewModel { ChatViewModel(get(), get()) }
    viewModel { BaseTabViewModel(get()) }
}

val serviceModule = module {
    single { FileService(get()) }
    single { LoginStatusService(get(), get(), get()) }
    single { Referrals(get()) }
    single { RemoteConfig() }
    single { TextKeys(get()) }
}

val repositoriesModule = module {
    single { ChatRepository(get(), get(), get()) }
    single { com.hedvig.app.data.chat.ChatRepository(get()) }
    single { DirectDebitRepository(get()) }
    single { ClaimsRepository(get()) }
    single { DashboardRepository(get()) }
    single { MarketingStoriesRepository(get(), get(), get()) }
    single { ProfileRepository(get()) }
    single { UserRepository(get()) }
}

val trackerModule = module {
    single { ClaimsTracker(get()) }
    single { DashboardTracker(get()) }
    single { MarketingTracker(get()) }
    single { ProfileTracker(get()) }
}
