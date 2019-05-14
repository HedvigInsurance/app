package com.hedvig.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hedvig.app.R
import com.hedvig.app.util.whenApiVersion
import timber.log.Timber

class PushNotificationService : FirebaseMessagingService() {
    override fun onCreate() {
        super.onCreate()
        setupNotificationChannel()
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
        val pendingIntent = NavDeepLinkBuilder(this)
            .setGraph(R.navigation.logged_in_navigation)
            .setDestination(R.id.loggedInChatFragment)
            .createPendingIntent()

        val notification = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hedvig_symbol_android)
            .setContentTitle(resources.getString(R.string.NOTIFICATION_CHAT_TITLE))
            .setContentText(resources.getString(R.string.NOTIFICATION_CHAT_BODY))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setChannelId(NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)
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
