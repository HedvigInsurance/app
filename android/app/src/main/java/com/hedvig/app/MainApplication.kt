package com.hedvig.app

import android.content.Context
import android.support.v7.app.AppCompatDelegate
import com.apollographql.apollo.ApolloClient
import com.facebook.soloader.SoLoader
import com.google.android.play.core.splitcompat.SplitCompat
import com.google.android.play.core.splitcompat.SplitCompatApplication
import com.hedvig.app.service.TextKeys
import com.ice.restring.Restring
import com.jakewharton.threetenabp.AndroidThreeTen
import io.branch.referral.Branch
import net.ypresto.timbertreeutils.CrashlyticsLogExceptionTree
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : SplitCompatApplication() {

    val apolloClient: ApolloClient by inject()

    val textKeys: TextKeys by inject()

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        SplitCompat.install(this)
    }

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
                repositoriesModule
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
