package com.hedvig.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.work.Configuration
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.apollographql.apollo.ApolloClient
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hedvig.app.R
import com.hedvig.app.util.react.AsyncStorageNative
import com.hedvig.app.util.whenApiVersion
import dagger.android.AndroidInjection
import timber.log.Timber
import javax.inject.Inject

class PushNotificationService : FirebaseMessagingService() {

    @Inject
    lateinit var asyncStorageNative: AsyncStorageNative

    @Inject
    lateinit var apolloClient: ApolloClient

    override fun onCreate() {
        super.onCreate()
        AndroidInjection.inject(this)
        setupNotificationChannel()

        val workerFactory = object : WorkerFactory() {
            override fun createWorker(
                appContext: Context,
                workerClassName: String,
                workerParameters: WorkerParameters
            ) = PushNotificationWorker(appContext, workerParameters, apolloClient, asyncStorageNative)
        }
        WorkManager
            .initialize(this, Configuration.Builder().setWorkerFactory(workerFactory).build())
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
        sendChatMessageNotification()
    }

    private fun setupNotificationChannel() = whenApiVersion(Build.VERSION_CODES.O) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                resources.getString(R.string.NOTIFICATION_CHANNEL_NAME),
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = resources.getString(R.string.NOTIFICATION_CHANNEL_DESCRIPTION) }
        )
    }

    private fun sendChatMessageNotification() {
        val notification = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hedvig_symbol_android)
            .setContentTitle(resources.getString(R.string.NOTIFICATION_CHAT_TITLE))
            .setContentText(resources.getString(R.string.NOTIFICATION_CHAT_BODY))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setChannelId(NOTIFICATION_CHANNEL_ID)
            // Set an action here
            .build()

        NotificationManagerCompat
            .from(this)
            .notify(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_CHANNEL_ID = "hedvig-push"
        const val NOTIFICATION_ID = 1 // TODO: Better logic for this
    }
}
