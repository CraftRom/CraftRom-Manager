package com.craftrom.manager.utils

import android.os.Build
import android.os.Build.VERSION_CODES
import com.craftrom.manager.utils.root.RootUtils
import com.craftrom.manager.utils.root.RootUtils.getProp
import com.craftrom.manager.utils.root.RootUtils.runCommand
import java.lang.reflect.Field
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Created by willi on 31.12.15.
 */
object Device {
    fun getKernelVersion(extended: Boolean): String {
        return getKernelVersion(extended, true)
    }

    fun getKernelVersion(extended: Boolean, root: Boolean): String {
        val version: String = FileUtils.readFile("/proc/version", root).toString()
        if (extended)  {
            if (version != null) {
                return version
            }else{
                runCommand("uname -a")
            }
        }
        val matcher: Matcher = Pattern.compile("Linux version (\\S+).+").matcher(version)
        return if (matcher.matches() && matcher.groupCount() === 1) {
            matcher.group(1)
        } else "unknown"
    }

    val architecture: String
        get() = runCommand("uname -m").toString()
    val hardware: String
        get() = Build.HARDWARE
    val bootloader: String
        get() = Build.BOOTLOADER
    val baseBand: String
        get() = Build.getRadioVersion()
    val codename: String
        get() {
            var codeName = ""
            val fields: Array<Field> = VERSION_CODES::class.java.fields
            for (field in fields) {
                val fieldName: String = field.getName()
                var fieldValue = -1
                try {
                    fieldValue = field.getInt(Any())
                } catch (ignored: IllegalArgumentException) {
                } catch (ignored: IllegalAccessException) {
                } catch (ignored: NullPointerException) {
                }
                if (fieldValue == Build.VERSION.SDK_INT) {
                    codeName = fieldName
                    break
                }
            }
            return codeName
        }
    val sDK: Int
        get() = Build.VERSION.SDK_INT
    private val sBoardFormatters: HashMap<String, BoardFormatter> = HashMap()
    private val sBoardAliases: HashMap<String, String> = HashMap()
    private var BOARD: String? = null
    fun getBuildDisplayId(): String? {
        return Build.DISPLAY
    }

    fun getFingerprint(): String? {
        return Build.FINGERPRINT
    }

    fun getVersion(): String? {
        return Build.VERSION.RELEASE
    }

    fun getVendor(): String? {
        return Build.MANUFACTURER
    }

    fun getDeviceName(): String? {
        return Build.DEVICE
    }

    fun getModel(): String? {
        return Build.MODEL
    }

    class Input private constructor() {
        private val mItems: MutableList<Item> = ArrayList()
        val items: List<Item>
            get() = mItems

        fun supported(): Boolean {
            return mItems.size > 0
        }

        class Item private constructor(input: List<String>) {
            var bus: String? = null
            var vendor: String? = null
            var product: String? = null
            var version: String? = null
            var name: String? = null
            var sysfs: String? = null
            var handlers: String? = null

        }

        companion object {
            private var sInstance: Input? = null
            val instance: Input?
                get() {
                    if (sInstance == null) {
                        sInstance = Input()
                    }
                    return sInstance
                }
            private const val BUS_INPUT = "/proc/bus/input/devices"
        }


    }

    class ROMInfo private constructor() {
        var version: String? = null

        companion object {
            private var sInstance: ROMInfo? = null
            val instance: ROMInfo?
                get() {
                    if (sInstance == null) {
                        sInstance = ROMInfo()
                    }
                    return sInstance
                }
            private val sProps = arrayOf(
                    "ro.cm.version",
                    "ro.pa.version",
                    "ro.pac.version",
                    "ro.carbon.version",
                    "ro.slim.version",
                    "ro.mod.version")
        }

        init {
            for (prop in sProps) {
                this.version = getProp(prop)
                if (this.version != null && !version!!.isEmpty()) {
                    break
                }
            }
        }
    }

    class MemInfo private constructor() {
        private val MEMINFO: String
        val totalMem: Long
            get() = try {
                getItem("MemTotal").replace("[^\\d]".toRegex(), "").toLong() / 1024L
            } catch (ignored: NumberFormatException) {
                0
            }
        val availableMem: Long
            get() {
                return try {
                    getItem("MemAvailable").replace("[^\\d]".toRegex(), "").toLong() / 1024L
                } catch (ignored: NumberFormatException) {
                    0
                }
            }
        val items: List<String>
            get() {
                val list: MutableList<String> = ArrayList()
                try {
                    for (line in MEMINFO.split("\\r?\\n".toRegex()).toTypedArray()) {
                        list.add(line.split(":".toRegex()).toTypedArray()[0])
                    }
                } catch (ignored: Exception) {
                }
                return list
            }

        fun getItem(prefix: String?): String {
            try {
                for (line in MEMINFO.split("\\r?\\n".toRegex()).toTypedArray()) {
                    if (line.startsWith(prefix!!)) {
                        return line.split(":".toRegex()).toTypedArray()[1].trim { it <= ' ' }
                    }
                }
            } catch (ignored: Exception) {
            }
            return ""
        }

        companion object {
            private var sInstance: MemInfo? = null
            val instance: MemInfo?
                get() {
                    if (sInstance == null) {
                        sInstance = MemInfo()
                    }
                    return sInstance
                }
            private const val MEMINFO_PROC = "/proc/meminfo"
        }

        init {
            MEMINFO = FileUtils.readFile(MEMINFO_PROC).toString()
        }
    }

    class CPUInfo private constructor() {
        val cpuInfo: String
        val features: String
            get() {
                val features = getString("Features")
                return if (!features.isEmpty()) features else getString("flags")
            }
        val processor: String
            get() {
                val pro = getString("Processor")
                return if (!pro.isEmpty()) pro else getString("model name")
            }
        val vendor: String
            get() {
                val vendor = getString("Hardware")
                return if (!vendor.isEmpty()) vendor else getString("vendor_id")
            }

        private fun getString(prefix: String): String {
            try {
                for (line in cpuInfo.split("\\r?\\n".toRegex()).toTypedArray()) {
                    if (line.startsWith(prefix)) {
                        return line.split(":".toRegex()).toTypedArray()[1].trim { it <= ' ' }
                    }
                }
            } catch (ignored: Exception) {
            }
            return ""
        }

        companion object {
            private var sInstance: CPUInfo? = null
            val instance: CPUInfo?
                get() {
                    if (sInstance == null) {
                        sInstance = CPUInfo()
                    }
                    return sInstance
                }
            private const val CPUINFO_PROC = "/proc/cpuinfo"
        }

        init {
            cpuInfo = FileUtils.readFile(CPUINFO_PROC, false)!!
        }
    }

    class TrustZone private constructor() {
        companion object {
            private var sInstance: TrustZone? = null
            val instance: TrustZone?
                get() {
                    if (sInstance == null) {
                        sInstance = TrustZone()
                    }
                    return sInstance
                }
            private val PARTITIONS: HashMap<String?, String> = HashMap()

            init {
                PARTITIONS["/dev/block/platform/msm_sdcc.1/by-name/tz"] = "QC_IMAGE_VERSION_STRING="
                PARTITIONS["/dev/block/bootdevice/by-name/tz"] = "QC_IMAGE_VERSION_STRING="
            }
        }

        val version = ""


    }

    private interface BoardFormatter {
        fun format(board: String?): String?
    }


}
