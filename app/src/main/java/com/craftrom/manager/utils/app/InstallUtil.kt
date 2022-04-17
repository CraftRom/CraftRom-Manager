package com.craftrom.manager.utils.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import org.koin.core.component.KoinComponent
import java.io.File
import java.util.*

class InstallUtil: KoinComponent {

    private val fileProvider = "com.craftrom.manager.fileprovider"
    private val downloadDir = "downloads"
    private val mime = "application/vnd.android.package-archive"

    private fun clearOldFiles(context: Context) {
        val dir = File(context.cacheDir, downloadDir)
        dir.walkTopDown().filter { System.currentTimeMillis() - it.lastModified() > 1_200_000 }
            .forEach { it.delete() }
    }

    private fun getFile(context: Context): File {
            val dir = File(context.cacheDir, downloadDir)
            dir.mkdirs()
        return File(dir, UUID.randomUUID().toString())
    }

    @Suppress("DEPRECATION")
    private fun getInstallIntent(activity: Activity, file: File): Intent {
        return Intent(Intent.ACTION_INSTALL_PACKAGE).apply {
            setDataAndType(FileProvider.getUriForFile(activity, fileProvider, file), mime)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            putExtra(Intent.EXTRA_RETURN_RESULT, true)
        }
    }

    fun install(activity: Activity, file: File, id: Int): Boolean {
        activity.startActivityForResult(getInstallIntent(activity, file), id)
        return true
    }


}