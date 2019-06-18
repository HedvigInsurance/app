package com.hedvig.app.react

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.content.LocalBroadcastManager
import androidx.navigation.Navigation
import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.rx2.Rx2Apollo
import com.facebook.react.bridge.LifecycleEventListener
import com.facebook.react.bridge.Promise
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.ReadableType
import com.google.firebase.analytics.FirebaseAnalytics
import com.hedvig.android.owldroid.graphql.InsuranceStatusQuery
import com.hedvig.android.owldroid.type.InsuranceStatus
import com.hedvig.app.LoggedInActivity
import com.hedvig.app.R
import com.hedvig.app.feature.chat.UploadBottomSheet
import com.hedvig.app.feature.dashboard.ui.PerilBottomSheet
import com.hedvig.app.feature.dashboard.ui.PerilIcon
import com.hedvig.app.feature.offer.OfferActivity
import com.hedvig.app.feature.offer.OfferChatOverlayFragment
import com.hedvig.app.feature.referrals.PromotionCodeBottomSheet
import com.hedvig.app.util.extensions.proxyNavigate
import com.hedvig.app.util.extensions.setIsLoggedIn
import com.hedvig.app.util.extensions.triggerRestartActivity
import com.hedvig.app.util.react.AsyncStorageNative
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.plusAssign
import timber.log.Timber

class ActivityStarterModule(
    reactContext: ReactApplicationContext,
    private val apolloClient: ApolloClient,
    private val asyncStorageNative: AsyncStorageNative
) : ReactContextBaseJavaModule(reactContext), LifecycleEventListener {

    private val disposables = CompositeDisposable()

    private val fragmentManager: FragmentManager
        get() = (reactApplicationContext.currentActivity as FragmentActivity).supportFragmentManager

    private val firebaseAnalytics by lazy { FirebaseAnalytics.getInstance(reactApplicationContext) }

    private val localBroadcastManager = LocalBroadcastManager.getInstance(reactContext)

    private val fileUploadBroadcastReceiver = FileUploadBroadcastReceiver()

    private var fileUploadCallback: Promise? = null

    init {
        localBroadcastManager.registerReceiver(fileUploadBroadcastReceiver, IntentFilter(FILE_UPLOAD_INTENT))
    }

    override fun getName() = "ActivityStarter"

    override fun onHostResume() {
    }

    override fun onHostPause() {
    }

    override fun onHostDestroy() {
        localBroadcastManager.unregisterReceiver(fileUploadBroadcastReceiver)
        disposables.clear()
    }

    @ReactMethod
    fun navigateToOfferFromChat() {
        val activity = reactApplicationContext.currentActivity
        if (activity != null) {
            asyncStorageNative.setKey("@hedvig:isViewingOffer", "true")
            currentActivity?.let {
                it.startActivity(Intent(it, OfferActivity::class.java))
            }
        }
    }

    @ReactMethod
    fun navigateToChatFromOffer() {
        currentActivity?.let {
            it.startActivity(Intent(it, OfferActivity::class.java))
        }
    }

    @ReactMethod
    fun navigateToLoggedInFromChat() {
        currentActivity?.let { activity ->
            reactApplicationContext.setIsLoggedIn(true)
            val intent = Intent(activity, LoggedInActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            activity.startActivity(intent)
        }
    }

    @ReactMethod
    fun showOfferChatOverlay() {
        val offerChatOverlayFragment = OfferChatOverlayFragment()
        offerChatOverlayFragment.show(fragmentManager, "OfferChatOverlay")
    }

    @ReactMethod
    fun showPerilOverlay(subject: String, iconId: String, title: String, description: String) {
        PerilBottomSheet.newInstance(subject, PerilIcon.from(iconId), title, description)
            .show(fragmentManager, "perilSheet")
    }

    @ReactMethod
    fun showPromotionOverlay() {
        PromotionCodeBottomSheet.newInstance()
            .show(fragmentManager, "perilSheet")
    }

    @ReactMethod
    fun showFileUploadOverlay(onUpload: Promise) {
        val uploadBottomSheet = UploadBottomSheet()
        uploadBottomSheet.show(fragmentManager, "FileUploadOverlay")
        fileUploadCallback = onUpload
    }

    @ReactMethod
    fun logEvent(eventName: String, map: ReadableMap) =
        firebaseAnalytics.logEvent(eventName, convertMapToBundle(map))

    @ReactMethod
    fun restartApplication() =
        reactApplicationContext.currentActivity?.let {
            it.triggerRestartActivity(it::class.java)
        }

    @ReactMethod
    fun reloadChat() =
        localBroadcastManager.sendBroadcast(Intent(BROADCAST_RELOAD_CHAT))

    @ReactMethod
    fun doIsLoggedInProcedure() {
        disposables += Rx2Apollo
            .from(apolloClient.query(InsuranceStatusQuery()))
            .subscribe({ response ->
                response.data()?.insurance?.status?.let { status ->
                    when (status) {
                        InsuranceStatus.INACTIVE,
                        InsuranceStatus.INACTIVE_WITH_START_DATE,
                        InsuranceStatus.ACTIVE -> navigateToLoggedInFromChat()
                        InsuranceStatus.PENDING,
                        InsuranceStatus.TERMINATED,
                        InsuranceStatus.`$UNKNOWN` -> {
                            // Let the chat take care of this
                        }
                    }
                }
            }, { Timber.e(it) })
    }

    private fun convertMapToBundle(readableMap: ReadableMap): Bundle {
        val bundle = Bundle()
        val iterator = readableMap.keySetIterator()
        while (iterator.hasNextKey()) {
            val key = iterator.nextKey()
            when (readableMap.getType(key)) {
                ReadableType.Boolean -> bundle.putBoolean(key, readableMap.getBoolean(key))
                ReadableType.Number -> bundle.putDouble(key, readableMap.getDouble(key))
                ReadableType.String -> bundle.putString(key, readableMap.getString(key))
                ReadableType.Map -> bundle.putBundle(key, convertMapToBundle(readableMap.getMap(key)))
                else -> {
                    Timber.e("We don't cover null or array")
                }
            }
        }
        return bundle
    }

    private inner class FileUploadBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra(FILE_UPLOAD_RESULT)) {
                FILE_UPLOAD_SUCCESS -> {
                    fileUploadCallback?.resolve(intent.getStringExtra(FILE_UPLOAD_KEY))
                        ?: Timber.e("File upload successful but no callback present")
                    fileUploadCallback = null
                }
                FILE_UPLOAD_ERROR -> {
                    fileUploadCallback?.reject("E_NETWORK_ERROR", "failed to upload")
                        ?: Timber.e("File upload failed but no callback present") // TODO improve
                    fileUploadCallback = null
                }
            }
        }
    }

    companion object {
        const val BROADCAST_RELOAD_CHAT = "reloadChat"
        const val REDEEMED_CODE_BROADCAST = "redeemedCode"

        const val BROADCAST_MESSAGE_NAME = "message"

        const val MESSAGE_PROMOTION_CODE_REDEEMED = "promotionCodeRedeemed"

        const val FILE_UPLOAD_INTENT = "file_upload"
        const val FILE_UPLOAD_RESULT = "file_upload_result"
        const val FILE_UPLOAD_SUCCESS = "success"
        const val FILE_UPLOAD_ERROR = "error"
        const val FILE_UPLOAD_KEY = "key"
    }
}
