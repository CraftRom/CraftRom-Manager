package com.craftrom.manager.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log


class NLService : NotificationListenerService() {
    private val TAG = this.javaClass.simpleName
    private var mReceiver: NLServiceReceiver? = null
    override fun onCreate() {
        super.onCreate()
        mReceiver = NLServiceReceiver()
        val filter = IntentFilter()
        filter.addAction("com.craftrom.manager.NOTIFICATION_LISTENER_SERVICE_EXAMPLE")
        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        Log.i(TAG, "onNotificationPosted")
        Log.i(TAG, "ID :" + sbn.id + "\\t" + sbn.notification.tickerText + "\\t" + sbn.packageName)
        val intent = Intent("com.craftrom.manager.NOTIFICATION_LISTENER_EXAMPLE")
        intent.putExtra("notification_event", "onNotificationPosted:\\n" + sbn.packageName + "\\n")
        sendBroadcast(intent)
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification) {
        Log.i(TAG, "onNOtificationRemoved")
        Log.i(TAG, "ID :" + sbn.id + "\\t" + sbn.notification.tickerText + "\\t" + sbn.packageName)
        val intent = Intent("com.craftrom.manager.NOTIFICATION_LISTENER_EXAMPLE")
        intent.putExtra("notification_event", "onNotificationRemoved:\\n" + sbn.packageName + "\\n")
        sendBroadcast(intent)
    }

    internal inner class NLServiceReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.getStringExtra("command") == "clearall") {
                cancelAllNotifications()
            } else if (intent.getStringExtra("command") == "list") {
                val notificationIntent = Intent("com.craftrom.manager.NOTIFICATION_LISTENER_EXAMPLE")
                notificationIntent.putExtra("notification_event", "=======")
                sendBroadcast(notificationIntent)
                var i = 1
                for (sbn in this@NLService.activeNotifications) {
                    val infoIntent = Intent("com.craftrom.manager.NOTIFICATION_LISTENER_EXAMPLE")
                    infoIntent.putExtra(
                        "notification_event",
                        i.toString() + " " + sbn.packageName + "\\n"
                    )
                    sendBroadcast(infoIntent)
                    i++
                }
                val listIntent = Intent("com.craftrom.manager.NOTIFICATION_LISTENER_EXAMPLE")
                listIntent.putExtra("notification_event", "Notification List")
                sendBroadcast(listIntent)
            }
        }
    }
}