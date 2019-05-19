package com.hedvig.onboarding

import com.hedvig.app.isDebug
import com.hedvig.onboarding.chat.ChatRepository
import com.hedvig.onboarding.chat.ChatViewModel
import com.hedvig.onboarding.chat.UserRepository
import com.hedvig.onboarding.marketing.data.MarketingStoriesRepository
import com.hedvig.onboarding.marketing.service.MarketingTracker
import com.hedvig.onboarding.marketing.ui.MarketingStoriesViewModel
import com.hedvig.onboarding.react.AsyncStorageNative
import com.hedvig.onboarding.react.AsyncStorageNativeImpl
import okhttp3.OkHttpClient
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import timber.log.Timber

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        viewModelModule,
        repositoriesModule,
        trackerModule,
        applicationModule,
        serviceModule
    )
}

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
            //TODO: Fix
//            builder.addInterceptor(HttpLoggingInterceptor(HttpLoggingInterceptor.Logger { message ->
//                Timber.tag("OkHttp").i(message)
//            }).setLevel(HttpLoggingInterceptor.Level.BODY))
        }
        builder.build()
    }
}

val viewModelModule = module {
    viewModel { MarketingStoriesViewModel(get()) }
    viewModel { ChatViewModel(get(), get()) }
}

val serviceModule = module {
    single { LoginStatusService(get(), get(), get()) }
}

val repositoriesModule = module {
    single { ChatRepository(get(), get(), get()) }
    single { UserRepository(get()) }
    single { MarketingStoriesRepository(get(), get(), get()) }
}

val trackerModule = module {
    single { MarketingTracker(get()) }
}

