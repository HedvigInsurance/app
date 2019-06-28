package com.hedvig.app.react

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.content.LocalBroadcastManager
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.modules.core.DeviceEventManagerModule
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.hedvig.android.owldroid.graphql.MemberIdQuery
import com.hedvig.app.util.extensions.setIsLoggedIn
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber
import java.util.Date
import java.util.HashMap

class NativeRoutingModule constructor(
    reactContext: ReactApplicationContext,
    private val apolloClient: ApolloClient
) : ReactContextBaseJavaModule(reactContext), LifecycleEventListener {

    private val profileBroadcastReceiver = ProfileBroadcastReceiver()
    private val onBoardingBroadcastReceiver = OnBoardingBroadcastReceiver()

    private val localBroadcastManager = LocalBroadcastManager.getInstance(reactContext)

    private val deviceEventEmitter: DeviceEventManagerModule.RCTDeviceEventEmitter
        get() = reactApplicationContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)

    private val compositeDisposable = CompositeDisposable()

    init {
        reactContext.addLifecycleEventListener(this)
    }

    override fun getName() = "NativeRouting"

    override fun onHostResume() {
        localBroadcastManager.registerReceiver(profileBroadcastReceiver, IntentFilter("profileNavigation"))
        localBroadcastManager.registerReceiver(onBoardingBroadcastReceiver, IntentFilter(ON_BOARDING_INTENT_FILER))
    }

    override fun onHostPause() {
        localBroadcastManager.unregisterReceiver(profileBroadcastReceiver)
        localBroadcastManager.unregisterReceiver(onBoardingBroadcastReceiver)
    }

    override fun onHostDestroy() {
    }

    @ReactMethod
    fun userDidSign() {
        reactApplicationContext.setIsLoggedIn(true)
        val referralsStorage = currentActivity!!.getSharedPreferences("referrals", Context.MODE_PRIVATE)
        val referee = referralsStorage
            .getString("referee", null)
        val incentive = referralsStorage
            .getString("incentive", null)

        if (referee == null || incentive == null) {
            return
        }

        val memberIdQuery = MemberIdQuery()
        compositeDisposable +=
            Rx2Apollo.from(apolloClient.query(memberIdQuery)).subscribe({ response ->
                val data = response.data()
                if (data == null) {
                    Timber.e("Data was null when trying to accredit referral")
                    return@subscribe
                }
                val member = data.member
                val memberId = member.id
                if (memberId == null) {
                    Timber.e("memberId was null when trying to accredit referral")
                    return@subscribe
                }

                val b = Bundle()
                b.putString("invitedByMemberId", referee)
                b.putString("memberId", memberId)
                b.putString("incentive", incentive)

                FirebaseAnalytics
                    .getInstance(reactApplicationContext)
                    .logEvent("referrals_sign", b)

                val referralParameters = HashMap<String, String>()
                referralParameters["invitedByMemberId"] = referee
                referralParameters["memberId"] = memberId
                referralParameters["incentive"] = incentive
                referralParameters["timestamp"] = java.lang.Long.toString(Date().time)

                FirebaseFirestore
                    .getInstance()
                    .collection("referrals")
                    .add(referralParameters)
                    .addOnSuccessListener { Timber.i("Successfully saved referral!") }
                    .addOnFailureListener { error -> Timber.e(error, "Failed to save referral") }

            }, { error -> Timber.e(error, "Failed to load memberId for referral") })
    }

    private fun logoutAndRestartApplication() {
        deviceEventEmitter.emit("NativeRoutingLogoutAndRestartApplication", null)
    }

    private fun restartChatOnBoarding() {
        deviceEventEmitter.emit("NativeRoutingRestartChatOnBoarding", null)
    }

    private inner class ProfileBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(NAVIGATE_ROUTING_EXTRA_NAME_ACTION)) {
                NAVIGATE_ROUTING_EXTRA_VALUE_LOGOUT_AND_RESTART_APPLICATION -> {
                    logoutAndRestartApplication()
                }
            }
        }
    }

    private inner class OnBoardingBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(NAVIGATE_ROUTING_EXTRA_NAME_ACTION)) {
                NAVIGATE_ROUTING_EXTRA_VALUE_LOGOUT_AND_RESTART_APPLICATION -> logoutAndRestartApplication()
                NAVIGATE_ROUTING_EXTRA_VALUE_RESTART_CHAT_ON_BOARDING -> restartChatOnBoarding()
            }
        }
    }

    companion object {

        const val ON_BOARDING_INTENT_FILER = "on_boarding_intent_filter"

        const val NAVIGATE_ROUTING_EXTRA_NAME_ACTION = "action"

        const val NAVIGATE_ROUTING_EXTRA_VALUE_LOGOUT_AND_RESTART_APPLICATION = "logoutAndRestartApplication"
        const val NAVIGATE_ROUTING_EXTRA_VALUE_RESTART_CHAT_ON_BOARDING = "restartChatOnBoarding"
    }
}
