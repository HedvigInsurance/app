package com.hedvig.app

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.Context
import android.support.multidex.MultiDex
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatDelegate
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import com.airbnb.android.react.lottie.LottiePackage
import com.apollographql.apollo.ApolloClient
import com.facebook.react.ReactApplication
import com.facebook.react.ReactNativeHost
import com.facebook.react.shell.MainReactPackage
import com.facebook.soloader.SoLoader
import com.hedvig.app.di.DaggerApplicationComponent
import com.hedvig.app.react.ActivityStarterReactPackage
import com.hedvig.app.react.NativeRoutingPackage
import com.hedvig.app.service.TextKeys
import com.hedvig.app.util.react.AsyncStorageNative
import com.horcrux.svg.SvgPackage
import com.ice.restring.Restring
import com.imagepicker.ImagePickerPackage
import com.jakewharton.threetenabp.AndroidThreeTen
import com.leo_pharma.analytics.AnalyticsPackage
import com.lugg.ReactNativeConfig.ReactNativeConfigPackage
import com.rnfs.RNFSPackage
import com.rnim.rn.audio.ReactNativeAudioPackage
import com.zmxv.RNSound.RNSoundPackage
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import dagger.android.HasServiceInjector
import dagger.android.support.HasSupportFragmentInjector
import io.branch.referral.Branch
import io.branch.rnbranch.RNBranchPackage
import io.sentry.RNSentryPackage
import net.ypresto.timbertreeutils.CrashlyticsLogExceptionTree
import timber.log.Timber
import javax.inject.Inject

class MainApplication : Application(), ReactApplication, HasActivityInjector, HasSupportFragmentInjector,
    HasServiceInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var fragmentInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var serviceInjector: DispatchingAndroidInjector<Service>

    @Inject
    lateinit var apolloClient: ApolloClient

    @Inject
    lateinit var workerFactory: WorkerFactory

    @Inject
    lateinit var asyncStorageNative: AsyncStorageNative

    @Inject
    lateinit var textKeys: TextKeys

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
        DaggerApplicationComponent
            .builder()
            .application(this)
            .build()
            .inject(this)
        Branch.getAutoInstance(this)
        SoLoader.init(this, false)
        // TODO Remove this probably? Or figure out a better solve for the problem
        if (BuildConfig.DEBUG || BuildConfig.APP_ID == "com.hedvig.test.app") {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsLogExceptionTree())
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        WorkManager
            .initialize(this, Configuration.Builder().setWorkerFactory(workerFactory).build())

        Restring.init(this)
        try {
            textKeys.refreshTextKeys()
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    override fun activityInjector() = activityInjector
    override fun supportFragmentInjector() = fragmentInjector
    override fun serviceInjector() = serviceInjector
}
