package com.craftrom.manager.utils.app

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.craftrom.manager.MainApplication
import com.craftrom.manager.R

class NotificationUtil(private val context: Context) {

    private val notificationManager get() = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private val channelId = context.getString(R.string.notification_channel_id)
    private val channelName = context.getString(R.string.notification_channel_name)
    private val updateTitle = context.getString(R.string.notification_update_title)
    private val updateAction = context.getString(R.string.notification_update_action)
    private val updateId = 28132

    fun showUpdateNotification() {


        // Intent for the notification click
        val intent = Intent(context, MainApplication::class.java).apply {
            flags = FLAG_ACTIVITY_REORDER_TO_FRONT
            action = updateAction
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_update)
            .setContentTitle(updateTitle)
            .setContentText(context.resources.getString(R.string.notification_update_description))
            .setContentIntent(PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE))

        createNotificationChannel()
        NotificationManagerCompat.from(context).notify(updateId, builder.build())
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH).apply {
            description = context.getString(R.string.notification_channel_description)
        }
        channel.setSound(null, null)
        notificationManager.createNotificationChannel(channel)
    }
}