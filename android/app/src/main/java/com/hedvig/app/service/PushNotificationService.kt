package com.hedvig.app.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import android.support.v4.app.TaskStackBuilder
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.hedvig.app.R
import com.hedvig.app.SplashActivity
import com.hedvig.app.feature.referrals.ReferralsSuccessfulInviteActivity
import com.hedvig.app.util.interpolateTextKey
import com.hedvig.app.feature.chat.ChatActivity
import com.hedvig.app.util.whenApiVersion
import timber.log.Timber

class PushNotificationService : FirebaseMessagingService() {

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

    override fun onMessageReceived(remoteMessage: RemoteMessage?) = when (remoteMessage?.data?.get(NOTIFICATION_TYPE_KEY)) {
        NOTIFICATION_TYPE_NEW_MESSAGE -> {
            setupNotificationChannel(
                NOTIFICATION_CHAT_CHANNEL_ID,
                resources.getString(R.string.NOTIFICATION_CHAT_CHANNEL_NAME),
                resources.getString(R.string.NOTIFICATION_CHAT_CHANNEL_DESCRIPTION)
            )
            sendChatMessageNotification()
        }
        NOTIFICATION_TYPE_REFERRAL_SUCCESS -> {
            setupNotificationChannel(
                NOTIFICATION_REFERRAL_CHANNEL_ID,
                resources.getString(R.string.NOTIFICATION_REFERRAL_CHANNEL_NAME),
                resources.getString(R.string.NOTIFICATION_REFERRAL_CHANNEL_DESCRIPTION)
            )
            sendReferralsNotification(remoteMessage)
        }
        else -> {
            setupNotificationChannel(
                NOTIFICATION_CHAT_CHANNEL_ID,
                resources.getString(R.string.NOTIFICATION_CHANNEL_NAME),
                resources.getString(R.string.NOTIFICATION_CHANNEL_DESCRIPTION)
            )
            val title = remoteMessage?.data?.get(DATA_MESSAGE_TITLE)
                ?: resources.getString(R.string.NOTIFICATION_CHAT_TITLE)
            val body = remoteMessage?.data?.get(DATA_MESSAGE_BODY)
                ?: resources.getString(R.string.NOTIFICATION_CHAT_BODY)
            sendDefaultNotification(title, body)
        }
    }

    private fun setupNotificationChannel(channelId: String, channelName: String, channelDescription: String) = whenApiVersion(Build.VERSION_CODES.O) {
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = channelDescription }
        )
    }

    private fun sendChatMessageNotification() {
        val chatIntent = Intent(this, ChatActivity::class.java)
        chatIntent.putExtra(ChatActivity.EXTRA_SHOW_CLOSE, true)

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(chatIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notification = NotificationCompat
            .Builder(this, NOTIFICATION_CHAT_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hedvig_symbol_android)
            .setContentTitle(resources.getString(R.string.NOTIFICATION_CHAT_TITLE))
            .setContentText(resources.getString(R.string.NOTIFICATION_CHAT_BODY))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setChannelId(NOTIFICATION_CHAT_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat
            .from(this)
            .notify(NOTIFICATION_CHAT_ID, notification)
    }

    private fun sendReferralsNotification(remoteMessage: RemoteMessage?) {
        val referralsIntent = Intent(this, ReferralsSuccessfulInviteActivity::class.java)

        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(referralsIntent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        val notificationBuilder = NotificationCompat
            .Builder(this, NOTIFICATION_REFERRAL_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hedvig_symbol_android)
            .setContentTitle(resources.getString(R.string.NOTIFICATION_REFERRAL_COMPLETED_TITLE))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setChannelId(NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)

        val contentText = remoteMessage?.data?.get(DATA_MESSAGE_REFERRED_SUCCESS_NAME)?.let {
            interpolateTextKey(
                resources.getString(R.string.NOTIFICATION_REFERRAL_COMPLETED_CONTENT_WITH_NAME),
                "NAME" to it
            )
        } ?: run {
            resources.getString(R.string.NOTIFICATION_REFERRAL_COMPLETED_CONTENT)
        }
        notificationBuilder.setContentText(contentText)

        NotificationManagerCompat
            .from(this)
            .notify(NOTIFICATION_REFERRAL_ID, notificationBuilder.build())
    }

    private fun sendDefaultNotification(title: String, body: String) {
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, SplashActivity::class.java),
            PendingIntent.FLAG_ONE_SHOT
        )

        val notification = NotificationCompat
            .Builder(this, NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hedvig_symbol_android)
            .setContentTitle(title)
            .setContentText(body)
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
        const val NOTIFICATION_ID = 0

        const val NOTIFICATION_CHAT_CHANNEL_ID = "hedvig-chat"
        const val NOTIFICATION_CHAT_ID = 1

        const val NOTIFICATION_REFERRAL_CHANNEL_ID = "hedvig-referral"
        const val NOTIFICATION_REFERRAL_ID = 2

        const val NOTIFICATION_TYPE_KEY = "TYPE"
        const val NOTIFICATION_TYPE_NEW_MESSAGE = "NEW_MESSAGE"
        const val NOTIFICATION_TYPE_REFERRAL_SUCCESS = "REFERRAL_SUCCESS"

        const val DATA_MESSAGE_TITLE = "DATA_MESSAGE_TITLE"
        const val DATA_MESSAGE_BODY = "DATA_MESSAGE_BODY"

        const val DATA_MESSAGE_REFERRED_SUCCESS_NAME = "DATA_MESSAGE_REFERRED_SUCCESS_NAME"
    }
}
