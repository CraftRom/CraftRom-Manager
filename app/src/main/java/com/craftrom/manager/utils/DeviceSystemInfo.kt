package com.craftrom.manager.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import com.craftrom.manager.R
import java.text.SimpleDateFormat
import java.util.*

open class DeviceSystemInfo{

    companion object {
        private val context: Context
            get() {
                TODO()
            }

        fun model(): String = Build.MODEL

        fun product(): String = Build.PRODUCT

        fun display(): String = Build.DISPLAY

        fun id(): String = Build.ID

        fun tags(): String = Build.TAGS

        fun brand(): String = Build.BRAND

        fun device():String = Build.DEVICE

        fun type(): String = Build.TYPE

        fun manufacturer(): String = Build.MANUFACTURER

        fun board(): String = Build.BOARD

        fun hardware(): String = Build.HARDWARE

        fun releaseVersion(): String = Build.VERSION.RELEASE

        fun apiLevel(): Int = Build.VERSION.SDK_INT

        fun user(): String = Build.USER

        fun host(): String = Build.HOST

        fun fingerprint(): String = Build.FINGERPRINT

        fun bootloader(): String = Build.BOOTLOADER

        fun arch(): String {
            val arch = System.getProperty("os.arch")
            return if (!arch.isNullOrEmpty()) arch else errorResult()
        }

        fun kernelVersion(): String {
            val kernelVersion = System.getProperty("os.version")
            return if (!kernelVersion.isNullOrEmpty()) kernelVersion else errorResult()
        }

        fun chidoriVersion(): String = kernelVersion().substring(
            kernelVersion().lastIndexOf(".")).substring(1, 4)

        fun chidoriName(): String = kernelVersion().substring(
            kernelVersion().indexOf("-")).substring(1, 8)

        @SuppressLint("SimpleDateFormat")
        fun date(): String {
            val date = Date(Build.TIME)
            val dateFormat = SimpleDateFormat("dd/MM/yy")
            return dateFormat.format(date)
        }

        fun codeName(): String {
            val fields = Build.VERSION_CODES::class.java.fields
            return fields[Build.VERSION.SDK_INT].name
        }

        fun deviceCode(): String {
            val device = device()
            val code: String
            val juice = mutableListOf("citrus", "lime", "lemon", "pomelo")
            val olives = mutableListOf("olive", "olivewood", "olivelite")
            val onclite = mutableListOf("onc", "onclite")
            val surya = mutableListOf("karna", "surya")
            code = if (isEliminated(device, juice)) {
                "juice"
            } else {
                if (isEliminated(device, onclite)) {
                    "onclite"
                } else {
                    if (isEliminated(device, surya)) {
                        "surya"
                    } else {
                        if (isEliminated(device, olives)) {
                            "olives"
                        } else {
                            device
                        }
                    }
                }
            }

            return code
        }



    private fun isEliminated(name: String, device: MutableList<String>): Boolean {
            return name in device
        }

        fun errorResult() = context.getString(R.string.common_empty_result)

}
}