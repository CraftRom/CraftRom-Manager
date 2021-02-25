package com.craftrom.manager.utils.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.topjohnwu.superuser.internal.Utils.context


class NotificationUtils {
    companion object {
        val NOTIFICATION_ID = 101
        val CHANNEL_ID = "channelID"
        val CHANEL_NAME = "Chidori Kernel"

        fun notify(title: String?, message: String?) {
            // Create PendingIntent
            val intent = Intent(context, MainActivity::class.java)
            val resultPendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            // Create Notification
            val notificationChannel: NotificationChannel = NotificationChannel(CHANNEL_ID, CHANEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationChannel.description = "Test"
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(resultPendingIntent)
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)


           // Show Notification
            notificationManager.notify(NOTIFICATION_ID, builder.build())
        }
    }
}