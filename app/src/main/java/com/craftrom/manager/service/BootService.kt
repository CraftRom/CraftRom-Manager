package com.craftrom.manager.service

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.craftrom.manager.R
import com.craftrom.manager.splash.SplashActivity
import com.craftrom.manager.utils.Constants.Companion.NOTIFICATION_CHANNEL_BOOT

class BootService: Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationChannel = NotificationChannel(NOTIFICATION_CHANNEL_BOOT, "Check Cidori Kernel update", NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.setSound(null, null)
        notificationManager.createNotificationChannel(notificationChannel)

        val builder = Notification.Builder(this, NOTIFICATION_CHANNEL_BOOT)
        builder.setContentTitle("Check Cidori Kernel update")
                .setSmallIcon(R.drawable.ic_tile_default)

        startForeground(1, builder.build())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val bool = applyOnBoot(this);
        if (!bool)
            stopSelf()

        return START_NOT_STICKY
    }

    private fun applyOnBoot(service: BootService): Boolean {
        val delay = 5
        val launchIntent = Intent(service, SplashActivity::class.java)
        val contentIntent = PendingIntent.getActivity(service,0, launchIntent, 0)

        val builder = Notification.Builder(service, NOTIFICATION_CHANNEL_BOOT)
        builder.setContentTitle(service.getString(R.string.app_name))
                .setContentText(service.getString(R.string.test, delay))
                .setSmallIcon(R.drawable.ic_tile_default)
                .setOngoing(true)
                .setWhen(0)

        val builderComplete = Notification.Builder(service, NOTIFICATION_CHANNEL_BOOT)
        builderComplete.setContentTitle(service.getString(R.string.app_name))
                .setSmallIcon(R.drawable.ic_tile_default)
                .setContentIntent(contentIntent)
                .setAutoCancel(true)

        val handler =  Handler(Looper.getMainLooper())

        Thread(Runnable {
            for (i in 0..delay) {
                builder.setContentText(service.getString(R.string.test, delay-i))
                        .setProgress(delay, i, false)
                service.startForeground(1, builder.build())
                try {
                    Thread.sleep(1000)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            builderComplete.setContentTitle("Settings applied successfully")
            val manager = service.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(2, builderComplete.build())

            service.stopSelf()
        }).start()

        return true
    }

}