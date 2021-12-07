package com.craftrom.manager.services

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.res.Configuration
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.topjohnwu.superuser.ShellUtils
import kotlin.math.roundToInt
import kotlin.math.sqrt


class FPS : Service() {
    private var fps = ""
    private var fpsFilePath: String = ""
    private var gpuFreq = ""
    private var gpuFreqFilePath: String = ""
    private lateinit var tvFps: TextView
    private lateinit var tvGpuFreq: TextView
    private lateinit var layoutView: View
    private lateinit var windowManager: WindowManager

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        // Attach View To Left Top Corner
        val inflater: LayoutInflater = LayoutInflater.from(this)
        layoutView = inflater.inflate(R.layout.layout_overlay, null)

        tvFps = layoutView.findViewById(R.id.tv_fps)
        tvGpuFreq = layoutView.findViewById(R.id.tv_gpu)
        findFpsFilePath()
        findGpuFilePath()

        val params: WindowManager.LayoutParams =
            WindowManager.LayoutParams(-2, -2, 2038, 4980792, -3)
        params.gravity = Gravity.START or Gravity.TOP

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        windowManager.addView(layoutView, params)

        // Keep Alive The Service
        val notificationChannel = NotificationChannel(
            "Craft_stats_notification_channel",
            "Craft_stats_notification_channel",
            NotificationManager.IMPORTANCE_HIGH
        )
        notificationChannel.setSound(null, null)
        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager).createNotificationChannel(
            notificationChannel
        )
        val notificationBuilder = Notification.Builder(this, "Craft_stats_notification_channel")
        notificationBuilder.setContentTitle("Craft Rom FPS Meter")
            .setContentText("Keep FPS meter running...").setSmallIcon(R.drawable.ic_new)
        startForeground(69, notificationBuilder.build())

        // Update FPS Counts
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(object : Runnable {
            @SuppressLint("SetTextI18n")
            override fun run() {
                try {
                    fps = ShellUtils.fastCmd("cat $fpsFilePath")
                    this@FPS.tvFps.text = fps.toDouble().roundToInt().toString()
                } catch (ignored: Exception) {
                    fps = ShellUtils.fastCmd("cat $fpsFilePath").split(" ").toTypedArray()[1]
                    this@FPS.tvFps.text = fps.toDouble().roundToInt().toString()
                }
                try {
                    gpuFreq = ShellUtils.fastCmd("cat $gpuFreqFilePath").toInt().toString()
                    val gpu = gpuFreq.toInt()
                    val curGpuFreq = (gpu / 1000000.0).roundToInt()
                    this@FPS.tvGpuFreq.text = "$curGpuFreq MHz"
                } catch (ignored:Exception){
                    this@FPS.tvGpuFreq.text = "error"
                }
                handler.postDelayed(this, 1000)
            }
        }, 1000)

        FPSTile.Service.isRunning = true
    }


    // Find The FPS File From /sys
    private fun findFpsFilePath() {
            fpsFilePath = ShellUtils.fastCmd("find /sys -name measured_fps 2>/dev/null")
                .trim { it <= ' ' }.split("\n").minOrNull().toString()
                if (fpsFilePath == "") {
                fpsFilePath = ShellUtils.fastCmd("find /sys -name fps 2>/dev/null")
                    .trim { it <= ' ' }.split("\n").minOrNull().toString() }
            Log.e(Constants.TAG, "\n" +
                    "FPS file found: $fpsFilePath")
    }

    // Find The GPU File From /sys
    private fun findGpuFilePath() {
        gpuFreqFilePath = ShellUtils.fastCmd("find /sys -name gpuclk 2>/dev/null")
            .trim { it <= ' ' }.split("\n").minOrNull().toString()
        if (gpuFreqFilePath == "") {
            gpuFreqFilePath = ShellUtils.fastCmd("find /sys -name frequency 2>/dev/null")
                .trim { it <= ' ' }.split("\n").minOrNull().toString() }
        if (gpuFreqFilePath == "") {
            gpuFreqFilePath = ShellUtils.fastCmd("find /sys -name gpu_rate 2>/dev/null")
                .trim { it <= ' ' }.split("\n").minOrNull().toString() }
        if (gpuFreqFilePath == "") {
            gpuFreqFilePath = ShellUtils.fastCmd("find /sys -name cur_freq 2>/dev/null")
                .trim { it <= ' ' }.split("\n").minOrNull().toString() }
        Log.e(Constants.TAG, "GPU file found $gpuFreqFilePath")
    }


    override fun onDestroy() {
        super.onDestroy()

        // Stop Service
        FPSTile.Service.isRunning = true
        windowManager.removeView(layoutView)
    }
}