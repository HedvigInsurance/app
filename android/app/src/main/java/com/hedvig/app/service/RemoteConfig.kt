package com.hedvig.app.service

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton


class RemoteConfig {
    private val firebaseRemoteConfig = FirebaseRemoteConfig.getInstance()

    init {
        firebaseRemoteConfig.setDefaults(
            hashMapOf(
                "Referrals_Enabled" to false,
                "Referrals_Incentive" to DEFAULT_INCENTIVE,
                "DynamicLink_iOS_BundleId" to "",
                "DynamicLink_Domain_Prefix" to ""
            )
        )
    }

    fun fetch(): Observable<RemoteConfigData> {
        return Observable.create<RemoteConfigData> { emitter ->
            emitter.onNext(RemoteConfigData.from(firebaseRemoteConfig))

            firebaseRemoteConfig
                .fetchAndActivate()
                .addOnSuccessListener {
                    emitter.onNext(RemoteConfigData.from(firebaseRemoteConfig))
                }
                .addOnFailureListener { error ->
                    emitter.onError(error)
                }
        }
    }
    companion object {
        const val DEFAULT_INCENTIVE = 100L
    }
}

data class RemoteConfigData(
    val referralsEnabled: Boolean,
    val referralsIncentiveAmount: Int,
    val referralsIosBundleId: String,
    val referralsDomain: String
) {
    companion object {
        fun from(firebaseRemoteConfig: FirebaseRemoteConfig): RemoteConfigData = RemoteConfigData(
            firebaseRemoteConfig.getBoolean("Referrals_Enabled"),
            firebaseRemoteConfig.getLong("Referrals_Incentive").toInt(),
            firebaseRemoteConfig.getString("DynamicLink_iOS_BundleId"),
            firebaseRemoteConfig.getString("DynamicLink_Domain_Prefix")
        )
    }
}
