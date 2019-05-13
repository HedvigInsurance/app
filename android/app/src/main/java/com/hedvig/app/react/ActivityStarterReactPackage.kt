package com.hedvig.app.react

import com.apollographql.apollo.ApolloClient
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager
import com.hedvig.app.util.react.AsyncStorageNative
import java.util.ArrayList

class ActivityStarterReactPackage(
    private val apolloClient: ApolloClient,
    private val asyncStorageNative: AsyncStorageNative
) : ReactPackage {

    override fun createNativeModules(
        reactContext: ReactApplicationContext
    ): List<NativeModule> {
        val modules = ArrayList<NativeModule>()
        modules.add(ActivityStarterModule(reactContext, apolloClient, asyncStorageNative))
        return modules
    }

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> {
        return emptyList()
    }
}
