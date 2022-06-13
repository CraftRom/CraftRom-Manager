package com.craftrom.manager.utils

import android.os.Build
import android.text.TextUtils
import android.util.Log
import androidx.annotation.StringDef
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.*


object RomUtils {

    private val TAG = "RomUtils"

    const val ROM_BLACKSHARK = "JOYUI"
    const val ROM_MIUI = "MIUI"
    const val ROM_EMUI = "EMUI"
    const val ROM_VIVO = "FUNTOUCH OS"
    const val ROM_OPPO = "COLOR OS"
    const val ROM_ONEPLUS = "OXYGEN"
    const val ROM_REALME = "REALME UI    "
    const val ROM_FLYME = "FLYME"
    const val ROM_SMARTISAN = "SMARTISAN"
    const val ROM_QIKU = "QIKU"
    const val ROM_LETV = "LETV"
    const val ROM_LENOVO = "LENOVO"
    const val ROM_NUBIA = "NUBIAUI"
    const val ROM_ZTE = "ZTE"
    const val ROM_COOLPAD = "COOLPAD"
    const val ROM_ROG = "REPLIBLIC"
    const val ROM_SAMSUNG = "ONEUI"
    const val ROM_UNKNOWN = "AOSP"

    private const val SYSTEM_VERSION_BLACKSHARK = "ro.blackshark.rom"
    private const val SYSTEM_VERSION_VIVO = "ro.vivo.os.version"
    private const val SYSTEM_VERSION_FLYME = "ro.build.display.id"
    private const val SYSTEM_VERSION_SMARTISAN = "ro.smartisan.version"
    private const val SYSTEM_VERSION_LETV = "ro.letv.eui"
    private const val SYSTEM_VERSION_LENOVO = "ro.lenovo.lvp.version"
    private const val SYSTEM_VERSION_REALME = "ro.build.version.realmeui"
    private const val SYSTEM_VERSION_ROG = "ro.build.fota.version"
    private const val SYSTEM_VERSION_SAMSUNG = "ro.channel.officehubrow"
    private const val SYSTEM_VERSION_ONEPLUS = "ro.build.ota.versionname"
    private const val SYSTEM_VERSION_OPPO = "ro.build.version.opporom"

    @RomName
    fun getRomName(): String {
        if (isBlacksharkRom()) {
            return ROM_BLACKSHARK
        }
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
        if (isSamsungRom()) {
            return ROM_SAMSUNG
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
        if (isOnePlusRom()) {
            return ROM_ONEPLUS
        }
        if (isRogRom()) {
            return ROM_ROG
        }
        if (isRealmeRom()) {
            return ROM_REALME
        }
        if (isZTERom()) {
            return ROM_ZTE
        }
        if (isCoolPadRom()) {
            return  ROM_COOLPAD
        }
        return ROM_UNKNOWN
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

    fun isBlacksharkRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_BLACKSHARK))
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
        return isRightRom("360", "QiKU", "quiku")
    }

    fun isOppoRom(): Boolean {
                //https://github.com/zhaozepeng/FloatWindowPermission/pull/26
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_OPPO)) && isRightRom("OPPO", "oppo")
    }

    fun isRealmeRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_REALME)) &&
                isRightRom("REALME", "realme")
    }

    fun isVivoRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_VIVO))
    }

    fun isSmartisanRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_SMARTISAN))
    }

    fun isSamsungRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_SAMSUNG)) &&
                isRightRom("SAMSUNG", "samsung")
    }

    fun isLetvRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_LETV))
    }

    fun isLenovoRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_LENOVO))
    }

    fun isOnePlusRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_ONEPLUS)) && (getSystemProperty(SYSTEM_VERSION_ROG)!!.lowercase().contains("Hydrogen")
                || getSystemProperty(SYSTEM_VERSION_ROG)!!.lowercase().contains("Oxygen"))
    }

    fun isRogRom(): Boolean {
        return !TextUtils.isEmpty(getSystemProperty(SYSTEM_VERSION_ROG)) && getSystemProperty(SYSTEM_VERSION_ROG)!!.lowercase().contains("CN_Phone")
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

    private fun isRightRom(vararg names: String): Boolean {
        for (name in names) {
            if (Build.MANUFACTURER.contains(name) || Build.BRAND.contains(name)) {
                return true
            }
        }
        return false
    }

    @StringDef(ROM_BLACKSHARK, ROM_MIUI, ROM_EMUI, ROM_VIVO, ROM_OPPO, ROM_ONEPLUS, ROM_REALME, ROM_FLYME, ROM_SMARTISAN, ROM_QIKU, ROM_LETV, ROM_LENOVO, ROM_NUBIA, ROM_ZTE, ROM_COOLPAD, ROM_ROG, ROM_SAMSUNG, ROM_UNKNOWN)
    @Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
    @Retention(AnnotationRetention.SOURCE)
    annotation class RomName
}