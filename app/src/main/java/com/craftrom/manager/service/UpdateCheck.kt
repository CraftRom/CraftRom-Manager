package com.craftrom.manager.service


import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.craftrom.manager.R
import com.craftrom.manager.fragments.kernel.KernelFragment
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.UtilsUpdater
import com.topjohnwu.superuser.internal.Utils.context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


class UpdateCheck : BroadcastReceiver() {
    lateinit var host: String
    var sp = context.getSharedPreferences("com.craftrom.manager", Context.MODE_PRIVATE)

    override fun onReceive(context: Context, intent: Intent) {
         if (Intent.ACTION_BOOT_COMPLETED.equals(intent.action)) {
            // Set a repeating alarm on boot to check for new updates once per day
            scheduleRepeatingUpdatesCheck(context)
         }

        if (!UtilsUpdater.isNetworkAvailable(context)) {
            Log.d(TAG, "Network not available, scheduling new check")
            scheduleUpdatesCheck(context)
            return
        }
        AndroidNetworking.initialize(com.topjohnwu.superuser.internal.Utils.context)
        val kernelVersion = readKernelVersion()



        val buildDate: Date  = SimpleDateFormat("MMM dd HH yyyy", Locale.ENGLISH).parse(kernelVersion.substring(kernelVersion.lastIndexOf("PREEMPT")).substring(12, 20)+ " ${Constants.CURRENT_YEAR}")

        AndroidNetworking
            .get(Constants.HOST_REFERENCE)
            .doNotCacheResponse()
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String) {
                    host = response
                    checkForUpdates(buildDate)
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        com.topjohnwu.superuser.internal.Utils.context,
                        "Please check your internet connection.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }

    fun checkForUpdates(buildDate: Date){
        AndroidNetworking
            .get("$host/kernel.json")
            .doNotCacheResponse()
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                @SuppressLint("SetTextI18n")
                override fun onResponse(response: JSONObject) {
                    val latestDate: Date = SimpleDateFormat("MMM dd HH yyyy", Locale.ENGLISH).parse(response.getString("latestDate") + " ${Constants.CURRENT_YEAR}")
                    val linuxVersion = response.getString("linuxVersion")
                    val editor = sp.edit()

                    if(latestDate.after(buildDate)){
                        editor.putString("linuxVersion", linuxVersion)
                        editor.apply()
                        showNotification(context)
                        updateRepeatingUpdatesCheck(context)

                    }
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        context,
                        "Please check your internet connection! ${anError?.errorDetail.toString()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }


    private fun readKernelVersion(): String {
        try {
            val p = Runtime.getRuntime().exec("uname -av")
            val `is`: InputStream? = if (p.waitFor() == 0) {
                p.inputStream
            } else {
                p.errorStream
            }
            val br = BufferedReader(
                InputStreamReader(`is`),
                32
            )
            val line = br.readLine()
            br.close()
            return line
        } catch (ex: Exception) {
            return "ERROR: " + ex.message
        }

    }

    companion object {
        private const val TAG = "UpdatesCheckReceiver"
        private const val DAILY_CHECK_ACTION = "daily_check_action"
        private const val ONESHOT_CHECK_ACTION = "oneshot_check_action"
        private const val NEW_UPDATES_NOTIFICATION_CHANNEL = "new_updates_notification_channel"
        private fun showNotification(context: Context) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationChannel = NotificationChannel(
                NEW_UPDATES_NOTIFICATION_CHANNEL,
                context.getString(R.string.new_update),
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationBuilder = NotificationCompat.Builder(
                context,
                NEW_UPDATES_NOTIFICATION_CHANNEL
            )
            notificationBuilder.setSmallIcon(R.drawable.ic_notification)
            val notificationIntent = Intent(context, KernelFragment::class.java)
            val intent = PendingIntent.getActivity(
                context, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT
            )
            notificationBuilder.setContentIntent(intent)
            notificationBuilder.setContentTitle(context.getString(R.string.new_update))
            notificationBuilder.setAutoCancel(true)
            notificationManager.createNotificationChannel(notificationChannel)
            notificationManager.notify(0, notificationBuilder.build())
        }

        private fun getRepeatingUpdatesCheckIntent(context: Context): PendingIntent {
            val intent = Intent(context, UpdateCheck::class.java)
            intent.action = DAILY_CHECK_ACTION
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        fun updateRepeatingUpdatesCheck(context: Context) {
            cancelRepeatingUpdatesCheck(context)
            scheduleRepeatingUpdatesCheck(context)
        }

        fun scheduleRepeatingUpdatesCheck(context: Context) {
            val updateCheckIntent = getRepeatingUpdatesCheckIntent(context)
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmMgr.setRepeating(
                AlarmManager.RTC,
                System.currentTimeMillis() +
                        UtilsUpdater.getUpdateCheckInterval(context),
                UtilsUpdater.getUpdateCheckInterval(context),
                updateCheckIntent
            )
            val nextCheckDate: Date = Date(
                System.currentTimeMillis() +
                        UtilsUpdater.getUpdateCheckInterval(context)
            )
            Log.d(
                TAG,
                "Setting automatic updates check: $nextCheckDate"
            )
        }

        fun cancelRepeatingUpdatesCheck(context: Context) {
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmMgr.cancel(getRepeatingUpdatesCheckIntent(context))
        }

        private fun getUpdatesCheckIntent(context: Context): PendingIntent {
            val intent = Intent(context, UpdateCheck::class.java)
            intent.action = ONESHOT_CHECK_ACTION
            return PendingIntent.getBroadcast(context, 0, intent, 0)
        }

        fun scheduleUpdatesCheck(context: Context) {
            val millisToNextCheck = AlarmManager.INTERVAL_HOUR * 2
            val updateCheckIntent = getUpdatesCheckIntent(context)
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmMgr[AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + millisToNextCheck] =
                updateCheckIntent
            val nextCheckDate = Date(System.currentTimeMillis() + millisToNextCheck)
            Log.d(
                TAG,
                "Setting one-shot updates check: $nextCheckDate"
            )
        }

        fun cancelUpdatesCheck(context: Context) {
            val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmMgr.cancel(getUpdatesCheckIntent(context))
            Log.d(TAG, "Cancelling pending one-shot check")
        }
    }
}