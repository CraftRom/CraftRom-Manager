package com.craftrom.manager.utils.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.github.kittinunf.fuel.core.ProgressCallback
import okhttp3.OkHttpClient
import okhttp3.Request
import okio.buffer
import okio.sink
import org.koin.core.component.KoinComponent
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

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


    suspend fun downloadAsync(context: Context, url: String, file: File = getFile(context), progress: ProgressCallback = { _, _ -> }) = suspendCoroutine<File> {
        clearOldFiles(context)
        get(url, file)
        it.resume(file)
    }

    fun install(activity: Activity, file: File, id: Int): Boolean {
        activity.startActivityForResult(getInstallIntent(activity, file), id)
        return true
    }

    fun get(url: String, file: File): File {
        val response = OkHttpClient.Builder().followRedirects(true).build().newCall(Request.Builder().url(url).build()).execute()
        if (!response.isSuccessful) throw IOException("Response not successful: ${response.code}")
        file.sink().buffer().apply { writeAll(response.body!!.source()) }.close()
        return file
    }

}