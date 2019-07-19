package com.hedvig.app

import android.app.Application
import android.content.Context
import android.support.multidex.MultiDex
import android.support.v7.app.AppCompatDelegate
import com.apollographql.apollo.ApolloClient
import com.facebook.soloader.SoLoader
import com.hedvig.app.service.TextKeys
import com.ice.restring.Restring
import com.jakewharton.threetenabp.AndroidThreeTen
import net.ypresto.timbertreeutils.CrashlyticsLogExceptionTree
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {

    val apolloClient: ApolloClient by inject()

    val textKeys: TextKeys by inject()


    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        MultiDex.install(this)
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
                repositoriesModule,
                trackerModule
            )
        }

        SoLoader.init(this, false)
        // TODO Remove this probably? Or figure out a better solve for the problem
        if (BuildConfig.DEBUG || BuildConfig.APP_ID == "com.hedvig.test.app") {
            Timber.plant(Timber.DebugTree())
        } else {
            Timber.plant(CrashlyticsLogExceptionTree())
        }

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)

        setupRestring()
    }

    private fun setupRestring() {
        val versionSharedPreferences =
            getSharedPreferences(LAST_OPENED_VERSION_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        if (versionSharedPreferences.contains(LAST_OPENED_VERSION)) {
            if (versionSharedPreferences.getInt(LAST_OPENED_VERSION, 0) != BuildConfig.VERSION_CODE) {
                getSharedPreferences("Restrings", Context.MODE_PRIVATE)
                    .edit()
                    .clear()
                    .apply()
            }
        } else {
            versionSharedPreferences
                .edit()
                .putInt(LAST_OPENED_VERSION, BuildConfig.VERSION_CODE)
                .apply()
        }
        Restring.init(this)
        try {
            textKeys.refreshTextKeys()
        } catch (exception: Exception) {
            Timber.e(exception)
        }
    }

    companion object {
        private const val LAST_OPENED_VERSION_SHARED_PREFERENCES = "last_opened_version_prefs"
        private const val LAST_OPENED_VERSION = "Last_opened_version"
    }
}
