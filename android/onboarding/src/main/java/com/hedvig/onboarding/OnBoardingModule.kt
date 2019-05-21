package com.hedvig.onboarding

import com.airbnb.android.react.lottie.LottiePackage
import com.facebook.react.ReactNativeHost
import com.facebook.react.shell.MainReactPackage
import com.hedvig.app.BuildConfig
import com.hedvig.onboarding.chat.ChatRepository
import com.hedvig.onboarding.chat.ChatViewModel
import com.hedvig.onboarding.chat.UserRepository
import com.hedvig.onboarding.react.ActivityStarterReactPackage
import com.hedvig.onboarding.react.NativeRoutingPackage
import com.horcrux.svg.SvgPackage
import com.leo_pharma.analytics.AnalyticsPackage
import com.lugg.ReactNativeConfig.ReactNativeConfigPackage
import com.rnfs.RNFSPackage
import com.rnim.rn.audio.ReactNativeAudioPackage
import com.zmxv.RNSound.RNSoundPackage
import io.branch.rnbranch.RNBranchPackage
import io.sentry.RNSentryPackage
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module

fun injectFeature() = loadFeature

private val loadFeature by lazy {
    loadKoinModules(
        applicationModule,
        viewModelModule,
        repositoriesModule
    )
}

val applicationModule = module {
    single<ReactNativeHost> {
        object : ReactNativeHost(get()) {
            override fun getUseDeveloperSupport() = BuildConfig.DEBUG

            override fun getPackages() = listOf(
                ActivityStarterReactPackage(get(), get()),
                MainReactPackage(),
//            ImagePickerPackage(),
                RNFSPackage(),
                SvgPackage(),
                ReactNativeConfigPackage(),
                RNSoundPackage(),
                RNSentryPackage(),
                RNBranchPackage(),
                ReactNativeAudioPackage(),
                AnalyticsPackage(),
                LottiePackage(),
                NativeRoutingPackage(get())
            )

            override fun getJSMainModuleName() = "index.android"
        }
    }
}

val viewModelModule = module {
    viewModel { ChatViewModel(get(), get()) }
}

val repositoriesModule = module {
    single { ChatRepository(get(), get(), get()) }
    single { UserRepository(get()) }
}

