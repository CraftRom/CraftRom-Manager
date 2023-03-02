package com.craftrom.manager.utils

import android.content.Context
import android.content.Intent

object Const {

    const val TAG = "CraftRom"
    const val KERNEL_NAME = "Chidori"
    const val KALI_NAME = "Tsukuyoumi"
    const val CURRENT_YEAR = "2022"
    const val RSS_FEED_LINK = "https://www.craft-rom.pp.ua/"
    const val DC_CONTENT_URL = "https://raw.githubusercontent.com/CraftRom/host_content/live/"
    const val CHIDORI_FILE_URL = "https://sourceforge.net/projects/exodusos/files/Chidori_Kernel/"
    const val EXODUS_FILE_URL = "https://sourceforge.net/projects/exodusos/files/ExodusOS/"


    const val PREF_KEY_ROOT_ENABLE = "root_enable"
    const val PREF_KEY_DEV_OPTIONS = "devOptions"
    fun getShare(context: Context, body: String?) {
        val intentShare = Intent(Intent.ACTION_SEND)
        intentShare.type = "text/plain"
        intentShare.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(intentShare, "Share with").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
    }

}

