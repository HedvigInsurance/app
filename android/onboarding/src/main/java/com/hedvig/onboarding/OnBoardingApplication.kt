package com.hedvig.onboarding

import com.airbnb.android.react.lottie.LottiePackage
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.shell.MainReactPackage
import com.hedvig.app.BuildConfig
import com.hedvig.app.MainApplication
import com.hedvig.onboarding.react.ActivityStarterReactPackage
import com.hedvig.onboarding.react.AsyncStorageNative
import com.hedvig.onboarding.react.NativeRoutingPackage
import com.horcrux.svg.SvgPackage
//import com.imagepicker.ImagePickerPackage
import com.leo_pharma.analytics.AnalyticsPackage
import com.lugg.ReactNativeConfig.ReactNativeConfigPackage
import com.rnfs.RNFSPackage
import com.rnim.rn.audio.ReactNativeAudioPackage
import com.zmxv.RNSound.RNSoundPackage
import io.branch.rnbranch.RNBranchPackage
import io.sentry.RNSentryPackage
import org.koin.android.ext.android.inject

class OnBoardingApplication : MainApplication(), ReactApplication {

    val asyncStorageNative: AsyncStorageNative by inject()

    override fun getReactNativeHost() = mReactNativeHost

    private val mReactNativeHost = object : ReactNativeHost(this) {
        override fun getUseDeveloperSupport() = BuildConfig.DEBUG

        override fun getPackages() = listOf(
            ActivityStarterReactPackage(apolloClient, asyncStorageNative),
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
            NativeRoutingPackage(apolloClient)
        )

        override fun getJSMainModuleName() = "index.android"
    }
}
