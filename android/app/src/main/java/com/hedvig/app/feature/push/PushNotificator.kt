package com.hedvig.app.feature.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import androidx.navigation.NavDeepLinkBuilder
import com.hedvig.app.R
import com.hedvig.app.util.whenApiVersion

class PushNotificator(
    private val context: Context
) {
    fun setupNotificationChannels() = whenApiVersion(Build.VERSION_CODES.O) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        notificationManager?.createNotificationChannel(
            NotificationChannel(
                CHAT_NOTIFICATION_CHANNEL_ID,
                context.resources.getString(R.string.NOTIFICATION_CHANNEL_NAME),
                NotificationManager.IMPORTANCE_HIGH
            ).apply { description = context.resources.getString(R.string.NOTIFICATION_CHANNEL_DESCRIPTION) }
        )
        //notificationManager?.createNotificationChannel((
        //    NotificationChannel(
        //        REFERRAL_COMPLETED_NOTIFICATION_CHANNEL_ID,
        //        "TODO",
        //        NotificationManager.IMPORTANCE_HIGH
        //    ).apply { description = "TODO" }
        //))
    }

    fun sendChatMessageNotification() {
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.logged_in_navigation)
            .setDestination(R.id.loggedInChatFragment)
            .createPendingIntent()

        val notification = NotificationCompat
            .Builder(context, CHAT_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hedvig_symbol_android)
            .setContentTitle(context.resources.getString(R.string.NOTIFICATION_CHAT_TITLE))
            .setContentText(context.resources.getString(R.string.NOTIFICATION_CHAT_BODY))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setChannelId(CHAT_NOTIFICATION_CHANNEL_ID)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat
            .from(context)
            .notify(CHAT_NOTIFICATION_ID, notification)
    }

    fun sendReferralCompletedNotification() {
        val pendingIntent = NavDeepLinkBuilder(context)
            .setGraph(R.navigation.root)

        val notification = NotificationCompat
            .Builder(context, REFERRAL_COMPLETED_NOTIFICATION_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_hedvig_symbol_android)
            .setContentTitle("TODO")
            .setContentText("TODO")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)
            .setChannelId(REFERRAL_COMPLETED_NOTIFICATION_CHANNEL_ID)
            .build()
    }

    companion object {
        const val CHAT_NOTIFICATION_ID = 1 // TODO: Better logic for this
        const val CHAT_NOTIFICATION_CHANNEL_ID = "hedvig-push"

        const val REFERRAL_COMPLETED_NOTIFICATION_ID = 2 // TODO: Come on now
        const val REFERRAL_COMPLETED_NOTIFICATION_CHANNEL_ID = "hedvig-referrals"
    }
}
