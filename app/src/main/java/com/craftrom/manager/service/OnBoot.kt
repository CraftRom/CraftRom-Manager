package com.craftrom.manager.service

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class OnBoot: BroadcastReceiver() {


     @SuppressLint("UnsafeProtectedBroadcastReceiver")
     override fun onReceive(context: Context?, intent: Intent?) {
        val sharedPreferences = context?.getSharedPreferences("update", Context.MODE_PRIVATE)
        if (sharedPreferences?.getBoolean("startup", true) != false)
            context?.startForegroundService(Intent(context, BootService::class.java))
    }

}