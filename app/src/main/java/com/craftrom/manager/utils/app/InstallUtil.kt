package com.craftrom.manager.utils.app

import android.content.Context
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



}