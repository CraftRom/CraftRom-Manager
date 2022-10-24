package com.craftrom.manager.utils

import android.content.Context
import android.content.Intent
import android.net.Uri

object Const {

    const val TAG = "CraftRom"
    const val KERNEL_NAME = "Chidori"
    const val CURRENT_YEAR = "2022"
    const val RSS_FEED_LINK = "https://www.craft-rom.pp.ua/"

    fun getShare(context: Context, body: String?) {
        val intentShare = Intent(Intent.ACTION_SEND)
        intentShare.type = "text/plain"
        intentShare.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(intentShare, "Share with").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

}

