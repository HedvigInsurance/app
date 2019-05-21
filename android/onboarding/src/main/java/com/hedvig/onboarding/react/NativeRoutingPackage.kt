package com.hedvig.onboarding.react

import com.apollographql.apollo.ApolloClient
import com.facebook.react.ReactPackage
import com.facebook.react.bridge.NativeModule
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.uimanager.ViewManager

class NativeRoutingPackage constructor(private val apolloClient: ApolloClient) : ReactPackage {

    override fun createNativeModules(reactContext: ReactApplicationContext): List<NativeModule> =
        listOf(NativeRoutingModule(reactContext, apolloClient))

    override fun createViewManagers(reactContext: ReactApplicationContext): List<ViewManager<*, *>> = emptyList()
}
