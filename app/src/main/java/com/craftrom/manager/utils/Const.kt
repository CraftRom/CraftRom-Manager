package com.craftrom.manager.utils

import android.content.Context
import android.content.Intent

object Const {

    const val TAG = "CraftRom"
    const val KERNEL_NAME = "Chidori"
    const val CURRENT_YEAR = "2022"
    const val RSS_FEED_LINK = "https://www.craft-rom.pp.ua/"

    fun share (context: Context, title: String, url: String){
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, url)
            type = "text/plain"
            // (Optional) Here we're setting the title of the content
            putExtra(Intent.EXTRA_TITLE, title)

            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, null)
        context.startActivity(share)
    }
}

