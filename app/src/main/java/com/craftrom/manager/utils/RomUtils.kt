package com.craftrom.manager.utils

import android.os.Build
import android.text.TextUtils
import android.util.Log
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*

object RomUtils {

    const val ROM_MIUI = "MIUI"
    const val ROM_EMUI = "EMUI"
    const val ROM_VIVO = "VIVO"
    const val ROM_OPPO = "OPPO"
    const val ROM_FLYME = "FLYME"
    const val ROM_SMARTISAN = "SMARTISAN"
    const val ROM_QIKU = "QIKU"
    const val ROM_LETV = "LETV"
    const val ROM_LENOVO = "LENOVO"
    const val ROM_NUBIA = "NUBIA"
    const val ROM_ZTE = "ZTE"
    const val ROM_COOLPAD = "COOLPAD"
    const val ROM_UNKNOWN = "AOSP"

    private const val SYSTEM_VERSION_VIVO = "ro.vivo.os.version"
    private const val SYSTEM_VERSION_FLYME = "ro.build.display.id"
    private const val SYSTEM_VERSION_SMARTISAN = "ro.smartisan.version"
    private const val SYSTEM_VERSION_LETV = "ro.letv.eui"
    private const val SYSTEM_VERSION_LENOVO = "ro.lenovo.lvp.version"

    private val TAG = "RomUtils"

    fun getRomName(): String {
        if (isMiuiRom()) {
            return ROM_MIUI
        }
        if (isHuaweiRom()) {
            return ROM_EMUI
        }
        if (isVivoRom()) {
            return ROM_VIVO
        }
        if (isOppoRom()) {
            return ROM_OPPO
        }
        if (isMeizuRom()) {
            return ROM_FLYME
        }
        if (isSmartisanRom()) {
            return ROM_SMARTISAN
        }
        if (is360Rom()) {
            return ROM_QIKU
        }
        if (isLetvRom()) {
            return ROM_LETV
        }
        if (isLenovoRom()) {
            return ROM_LENOVO
        }
        if (isZTERom()) {
            return ROM_ZTE
        }
        return if (isCoolPadRom()) {
            ROM_COOLPAD
        } else ROM_UNKNOWN
    }

    val emuiVersion: Double
        get() {
            try {
                val emuiVersion = getSystemProperty("ro.build.version.emui")
                val version = emuiVersion!!.substring(emuiVersion.indexOf("_") + 1)
                return java.lang.Double.parseDouble(version)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return 4.0
        }


    val miuiVersion: Int
        get() {
            val version = getSystemProperty("ro.miui.ui.version.name")
            if (version != null) {
                try {
                    return Integer.parseInt(version.substring(1))
                } catch (e: Exception) {
                    Log.e(TAG, "get miui version code error, version : $version")
                }

            }
            return -1
        }

    fun getSystemProperty(propName: String): String? {
        val line: String
        var input: BufferedReader? = null
        try {
            val p = Runtime.getRuntime().exec("getprop $propName")
            input = BufferedReader(InputStreamReader(p.inputStream), 1024)
            line = input.readLine()
            input.close()
        } catch (ex: IOException) {
            Log.e(TAG, "Unable to read sysprop $propName", ex)
            return null
        } finally {
            if (input != null) {
                try {
                    input.close()
                } catch (e: IOException) {
                    Log.e(TAG, "Exception while closing InputStream", e)
                }

            }
        }
        return line
    }

    fun isHuaweiRom(): Boolean {
        return Build.MANUFACTURER.contains("HUAWEI")
    }


    fun isMiuiRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty("ro.miui.ui.version.name"))
    }

    fun isMeizuRom(): Boolean {
        val meizuFlymeOSFlag = getSystemProperty(SYSTEM_VERSION_FLYME)!!
        return !TextUtils.isEmpty(meizuFlymeOSFlag) && meizuFlymeOSFlag.uppercase(Locale.getDefault())
            .contains(ROM_FLYME)
    }

    fun is360Rom(): Boolean {
        //fix issue https://github.com/zhaozepeng/FloatWindowPermission/issues/9
        return Build.MANUFACTURER.contains(ROM_QIKU) || Build.MANUFACTURER.contains("360") || Build.MANUFACTURER.contains("QiKU")
    }

    fun isOppoRom(): Boolean {
        //https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        return Build.MANUFACTURER.contains("OPPO") || Build.MANUFACTURER.contains("oppo")
    }

    fun isVivoRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_VIVO))
    }
    fun isSmartisanRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_SMARTISAN))
    }

    fun isLetvRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_LETV))
    }

    fun isLenovoRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_LENOVO))
    }

    fun isCoolPadRom(): Boolean {
        val model = Build.MODEL
        val fingerPrint = Build.FINGERPRINT
        return (!TextUtils.isEmpty(model) && model.lowercase(Locale.getDefault())
            .contains(ROM_COOLPAD)
                || !TextUtils.isEmpty(fingerPrint) && fingerPrint.lowercase(Locale.getDefault())
            .contains(ROM_COOLPAD))
    }

    fun isZTERom(): Boolean {
        val manufacturer = Build.MANUFACTURER
        val fingerPrint = Build.FINGERPRINT
        return (!TextUtils.isEmpty(manufacturer) && (fingerPrint.lowercase(Locale.getDefault())
            .contains(ROM_NUBIA)
                || fingerPrint.lowercase(Locale.getDefault()).contains(ROM_ZTE))
                || !TextUtils.isEmpty(fingerPrint) && (fingerPrint.lowercase(Locale.getDefault())
            .contains(ROM_NUBIA)
                || fingerPrint.lowercase(Locale.getDefault()).contains(ROM_ZTE)))
    }

}