package com.craftrom.manager.utils.updater.repository

import android.app.Activity
import android.content.IntentFilter
import android.view.LayoutInflater
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.craftrom.manager.BuildConfig
import com.craftrom.manager.MainApplication
import com.craftrom.manager.R
import com.craftrom.manager.utils.app.AppPrefs
import com.craftrom.manager.utils.app.InstallUtil
import com.craftrom.manager.utils.catchingAsync
import com.craftrom.manager.utils.downloader.DownloadManagerUtil
import com.craftrom.manager.utils.downloader.DownloadReceiver
import com.craftrom.manager.utils.ioScope
import com.craftrom.manager.utils.updater.selfupdate.SelfUpdateResponse
import com.github.kittinunf.fuel.Fuel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.kryptoprefs.invoke
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.coroutines.suspendCoroutine


class SelfUpdateRepository: KoinComponent {

    private val installer: InstallUtil by inject()
    private val prefs: AppPrefs by inject()
    private var downloadBroadcastReceiver: DownloadReceiver? = null

    private val url = "https://raw.githubusercontent.com/CraftRom/host_updater/android-10/apk.json"

    fun checkForUpdatesAsync(activity: Activity) = ioScope.catchingAsync {
            val r = Fuel.get(url).responseString().third.get()
            val o = Gson().fromJson(r, SelfUpdateResponse::class.java)
            prefs.selfUpdateCheck(System.currentTimeMillis())
            if (o.version > activity.packageManager.getPackageInfo(activity.packageName, 0).versionCode) {
                if (withContext(Dispatchers.Main) { showDialog(activity, o) }) {
                    installer.install(activity, installer.downloadAsync(activity, o.apk) { _, _ -> }, 0)
                }
            }
    }

    private suspend fun showDialog(activity: Activity, response: SelfUpdateResponse) = suspendCoroutine<Boolean> {

        // on below line we are creating a new bottom sheet dialog.
        val dialog = BottomSheetDialog(activity, R.style.ThemeBottomSheet)

        // on below line we are inflating a layout file which we have created.
        val card = LayoutInflater.from(activity).inflate(R.layout.dialog_install, null)

        val btnInstall = card.findViewById<Button>(R.id.btnInstall)
        val btnDownload = card.findViewById<Button>(R.id.btnDownload)
        val textInstall = card.findViewById<TextView>(R.id.installText)
        val textInfo = card.findViewById<TextView>(R.id.installInfo)
        val versionName = BuildConfig.VERSION_NAME
        val newsVersion = response.apkversion
        val title = activity.getString(R.string.app_name)
        val desc = "$versionName -> $newsVersion"


        textInstall.text = response.changelog
        textInfo.text = desc

        downloadBroadcastReceiver = DownloadReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction("android.intent.action.DOWNLOAD_COMPLETE")
        intentFilter.addAction("android.intent.action.DOWNLOAD_NOTIFICATION_CLICKED")
        activity.registerReceiver(downloadBroadcastReceiver, intentFilter)

        btnInstall.setOnClickListener{
            downloadInstall(activity, response.apk, title, response.apkversion.toString(), desc)
            dialog.dismiss()
        }

        btnDownload.setOnClickListener{
            downloadOnly(activity, response.apk, title, response.apkversion.toString(), desc)
            dialog.dismiss()
        }
        // below line is use to set cancelable to avoid
        // closing of dialog box when clicking on the screen.
        dialog.setCancelable(true)

        // on below line we are setting
        // content view to our view.
        dialog.setContentView(card)
        dialog.dismissWithAnimation = true
        // on below line we are calling
        // a show method to display a dialog.
        dialog.show()
    }

    private fun downloadOnly(activity: Activity, url: String, title: String, version: String, desc: String){
        activity.unregisterReceiver(downloadBroadcastReceiver)
        val dm = DownloadManagerUtil(activity)
        if (dm.checkDownloadManagerEnable()) {
            if (MainApplication.downloadId != 0L) {
                dm.clearCurrentTask(MainApplication.downloadId) // 先清空之前的下载
            }
            MainApplication.downloadId = dm.download(url, title, version,desc)
        }else{
            Toast.makeText(activity,"False download",Toast.LENGTH_SHORT).show()
        }
    }
    private fun downloadInstall(activity: Activity, url: String, title: String, version: String, desc: String){
        val dm = DownloadManagerUtil(activity)
        if (dm.checkDownloadManagerEnable()) {
            if (MainApplication.downloadId != 0L) {
                dm.clearCurrentTask(MainApplication.downloadId) // 先清空之前的下载
            }
            MainApplication.downloadId = dm.download(url, title, version, desc)
        }else{
            Toast.makeText(activity,"False download",Toast.LENGTH_SHORT).show()
        }
        }
}