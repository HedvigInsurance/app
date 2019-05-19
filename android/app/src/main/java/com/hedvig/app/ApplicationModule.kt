package com.hedvig.app

import android.content.Context
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.cache.normalized.NormalizedCacheFactory
import com.apollographql.apollo.cache.normalized.lru.EvictionPolicy
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCache
import com.apollographql.apollo.cache.normalized.lru.LruNormalizedCacheFactory
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.firebase.analytics.FirebaseAnalytics
import com.hedvig.common.owldroid.type.CustomType
import com.hedvig.app.data.debit.DirectDebitRepository
import com.hedvig.app.service.FileService
import com.hedvig.app.service.Referrals
import com.hedvig.app.service.RemoteConfig
import com.hedvig.app.service.TextKeys
import com.hedvig.common.util.apollo.ApolloTimberLogger
import com.hedvig.common.util.apollo.PromiscuousLocalDateAdapter
import com.hedvig.app.viewmodel.DirectDebitViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
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
}

val repositoriesModule = module {
    single { com.hedvig.app.data.chat.ChatRepository(get()) }
    single { DirectDebitRepository(get()) }
}
