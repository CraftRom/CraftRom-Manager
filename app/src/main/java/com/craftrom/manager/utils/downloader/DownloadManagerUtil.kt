package com.craftrom.manager.utils.downloader

import android.app.DownloadManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.widget.Toast

/**
 * 作者：Aaron
 * 时间：2018/9/21:8:41
 * 邮箱：
 * 说明：下载管理器
 */
class DownloadManagerUtil(private val mContext: Context) {

    /**
     * 可能会出错Cannot update URI: content://downloads/my_downloads/-1
     * 检查下载管理器是否被禁用
     */
    fun checkDownloadManagerEnable():Boolean {
        try {
            val state = mContext.packageManager.getApplicationEnabledSetting("com.android.providers.downloads")
            if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                    || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {
                val packageName = "com.android.providers.downloads"
                try {
                    val intent = Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    intent.data = Uri.parse("package:$packageName")
                    mContext.startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                    val intent = Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS)
                    mContext.startActivity(intent)
                }
                return false
            }
        } catch (e:Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }

    fun download(url: String, title: String, fileName: String, desc: String, downloadApp: Boolean): Long {
        val uri = Uri.parse(url)
        val req = DownloadManager.Request(uri)
        req.setTitle(title)
        req.setDescription(desc)
        req.setRequiresCharging(false)
        req.setAllowedOverMetered(true)
        req.setAllowedOverRoaming(true)
        req.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        req.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE or DownloadManager.Request.NETWORK_WIFI)
        if (downloadApp) {
            req.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
            req.setMimeType("application/vnd.android.package-archive")
        } else {
            req.setDestinationInExternalPublicDir(
                Environment.DIRECTORY_DOWNLOADS,
                fileName
            )
            req.setMimeType("application/zip")
        }
        req.setNotificationVisibility(1)

        val dm = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        return try {
            dm.enqueue(req)
        } catch (e: Exception) {
            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show()
            -1
        }
    }

    /**
     * 下载前先移除前一个任务，防止重复下载
     *
     * @param downloadId
     */
    fun clearCurrentTask(downloadId: Long) {
        val dm = mContext.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        try {
            dm.remove(downloadId)
        } catch (ex: IllegalArgumentException) {
            ex.printStackTrace()
        }
    }
}