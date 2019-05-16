package com.hedvig.app.feature.push

import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.inject
import timber.log.Timber

class PushNotificationService : FirebaseMessagingService() {
    private val pushNotificator: PushNotificator by inject()

    override fun onCreate() {
        super.onCreate()
        pushNotificator.setupNotificationChannels()
    }

    override fun onNewToken(token: String) {
        Timber.i("Acquired new token: %s", token)

        val work = OneTimeWorkRequest.Builder(PushNotificationWorker::class.java)
            .setInputData(
                Data.Builder()
                    .putString(PushNotificationWorker.PUSH_TOKEN, token)
                    .build()
            )
            .build()
        WorkManager
            .getInstance()
            .beginWith(work)
            .enqueue()
    }

    override fun onMessageReceived(p0: RemoteMessage?) {
        pushNotificator.sendChatMessageNotification()
    }
}
