package com.hedvig.app

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.support.v7.app.AppCompatDelegate
import com.airbnb.android.react.lottie.LottiePackage
import com.apollographql.apollo.ApolloClient
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.shell.MainReactPackage
import com.facebook.soloader.SoLoader
import com.hedvig.app.react.ActivityStarterReactPackage
import com.hedvig.app.react.NativeRoutingPackage
import com.hedvig.app.service.TextKeys
import com.hedvig.common.util.react.AsyncStorageNative
import com.horcrux.svg.SvgPackage
import com.ice.restring.Restring
import com.imagepicker.ImagePickerPackage
import com.jakewharton.threetenabp.AndroidThreeTen
import com.leo_pharma.analytics.AnalyticsPackage
import com.lugg.ReactNativeConfig.ReactNativeConfigPackage
import com.rnfs.RNFSPackage
import com.rnim.rn.audio.ReactNativeAudioPackage
import com.zmxv.RNSound.RNSoundPackage
import io.branch.referral.Branch
import io.branch.rnbranch.RNBranchPackage
import io.sentry.RNSentryPackage
import net.ypresto.timbertreeutils.CrashlyticsLogExceptionTree
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application(), ReactApplication {

    val apolloClient: ApolloClient by inject()

    val asyncStorageNative: AsyncStorageNative by inject()

    val textKeys: TextKeys by inject()

    private val mReactNativeHost = object : ReactNativeHost(this) {
        override fun getUseDeveloperSupport() = BuildConfig.DEBUG

        override fun getPackages() = listOf(
            ActivityStarterReactPackage(apolloClient, asyncStorageNative),
            MainReactPackage(),
            ImagePickerPackage(),
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

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    override fun getReactNativeHost() = mReactNativeHost

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(
                applicationModule,
                viewModelModule,
                serviceModule,
                repositoriesModule,
                trackerModule
            )
        }

        Branch.getAutoInstance(this)
        SoLoader.init(this, false)
        // TODO Remove this probably? Or figure out a better solve for the problem
        if (BuildConfig.DEBUG || BuildConfig.APP_ID == "com.hedvig.test.app") {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsLogExceptionTree())
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        Restring.init(this)
        try {
            textKeys.refreshTextKeys()
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }
}
