package com.craftrom.manager.service

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.service.notification.NotificationListenerService
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.notification.NotificationUtils
import com.topjohnwu.superuser.internal.Utils.context
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


abstract class UpdaterService  : NotificationListenerService() {

    private val mBroadcastReceiver: BroadcastReceiver? = null
    lateinit var host: String
    var sp = context.getSharedPreferences("com.craftrom.manager", Context.MODE_PRIVATE)

    override fun onCreate() {
        super.onCreate()

        val myIntent = Intent(context, NotifyKernelService()::class.java)
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val pendingIntent = PendingIntent.getService(context, 0, myIntent, 0)
        val calendar = Calendar.getInstance()
        calendar[Calendar.HOUR_OF_DAY] = 12
        calendar[Calendar.MINUTE] = 0
        calendar[Calendar.SECOND] = 0
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )

    }

  fun NotifyKernelService(){
        AndroidNetworking.initialize(context)
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
                        context,
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
                        NotificationUtils.notify(getString(R.string.new_update), getString(R.string.new_update_message) + " Cidori Kernel " + linuxVersion)
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

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver!!)
        super.onDestroy()
    }
}
