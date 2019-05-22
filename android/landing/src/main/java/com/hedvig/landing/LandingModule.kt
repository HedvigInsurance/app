package com.hedvig.landing

import android.content.Context
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.hedvig.app.service.LoginStatusService
import com.hedvig.landing.marketing.data.MarketingStoriesRepository
import com.hedvig.landing.marketing.service.MarketingTracker
import com.hedvig.landing.marketing.ui.MarketingStoriesViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.io.File

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        applicationModule,
        viewModelModule,
        repositoriesModule,
        trackerModule
    )
}

val applicationModule = module {
    single {
        SimpleCache(
            File(get<Context>().cacheDir, "hedvig_story_video_cache"),
            LeastRecentlyUsedCacheEvictor((10 * 1024 * 1024).toLong())
        )
    }
}

val viewModelModule = module {
    viewModel { MarketingStoriesViewModel(get()) }
}

val repositoriesModule = module {
    single { MarketingStoriesRepository(get(), get(), get()) }
}

val trackerModule = module {
    single { MarketingTracker(get()) }
}

