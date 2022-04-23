package com.craftrom.manager.utils

import `in`.sunilpaulmathew.sCommon.Utils.sUtils
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.util.DisplayMetrics
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.craftrom.manager.R
import com.craftrom.manager.utils.root.RootFile
import com.craftrom.manager.utils.root.RootUtils
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.io.SuFile
import java.io.File
import java.io.FileOutputStream
import java.math.BigDecimal
import java.math.RoundingMode
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


object Utils {

    fun rootCheck(ctx: Context?): Boolean {
        if (Shell.rootAccess()) return true
        Toast.makeText(ctx, "No Root !", Toast.LENGTH_SHORT).show()
        return false
    }

    fun getProp(prop: String): String {
        return ShellUtils.fastCmd("getprop $prop")
    }

    fun setProp(prop: String, value: String) {
        ShellUtils.fastCmd("setprop $prop $value")
    }

    fun needRebootToast(ctx: Context?) {
        Toast.makeText(ctx, "Take effect on next reboot", Toast.LENGTH_SHORT).show()
    }

    fun readAssetFile(context: Context?, file: String?): String {
        return sUtils.readAssetFile(file, context)
    }

    fun celsiusToFahrenheit(celsius: Double): Double {
        return 9.0 / 5.0 * celsius + 32
    }

    fun roundTo2Decimals(`val`: Double): Double {
        var bd = BigDecimal(`val`)
        bd = bd.setScale(2, RoundingMode.HALF_UP)
        return bd.toDouble()
    }

    fun strFormat(text: String?, vararg format: Any?): String {
        return String.format(text!!, *format)
    }

    fun strToFloat(text: String): Float {
        return try {
            text.toFloat()
        } catch (ignored: NumberFormatException) {
            0f
        }
    }

    fun strToLong(text: String): Long {
        return try {
            text.toLong()
        } catch (ignored: NumberFormatException) {
            0L
        }
    }

    fun strToInt(text: String): Int {
        return try {
            text.toInt()
        } catch (ignored: NumberFormatException) {
            0
        }
    }

    fun sToString(tSec: Long): String {
        val h = (tSec / (60 * 60)).toInt()
        val m = tSec.toInt() % (60 * 60) / 60
        val s = tSec.toInt() % (60 * 60) % 60
        var sDur = ""
        if (h != 0) sDur = h.toString() + "h "
        if (m != 0) sDur += m.toString() + "m "
        sDur += s.toString() + "s"
        return sDur
    }



    fun isPropRunning(key: String): Boolean {
        return try {
            RootUtils.runAndGetOutput("getprop | grep $key").split("]:").get(1).contains("running")
        } catch (ignored: Exception) {
            false
        }
    }

    fun hasProp(key: String): Boolean {
        return try {
            !RootUtils.runAndGetOutput("getprop | grep $key").isEmpty()
        } catch (ignored: Exception) {
            false
        }
    }

    fun writeFile(path: String, text: String?, append: Boolean, asRoot: Boolean) {
        if (asRoot) {
            RootFile.write(path, text!!, append)
            return
        }
        sUtils.create(text, File(path))
    }

    @JvmOverloads
    fun readFile(file: String, root: Boolean = true): String {
        return if (root) {
            RootFile.read(file)
        } else sUtils.read(File(file))
    }

    @JvmOverloads
    fun existFile(file: String, root: Boolean = true): Boolean {
        return if (!root) sUtils.exist(File(file)) else RootFile.exists(file)
    }

    fun create(text: String?, path: File?) {
        sUtils.create(text, path)
    }

    fun create(text: String, path: String) {
        RootUtils.runCommand("echo '$text' > $path")
    }

    fun mkdir(path: String?): Boolean {
        return SuFile.open(path).mkdirs()
    }

    fun append(text: String, path: String) {
        RootUtils.runCommand("echo '$text' >> $path")
    }

    @JvmOverloads
    fun delete(file: String, root: Boolean = true) {
        if (root) {
            RootFile.delete(file)
        } else {
            sUtils.delete(File(file))
        }
    }

    fun sleep(sec: Int) {
        sUtils.sleep(sec)
    }

    fun copy(source: String, dest: String) {
        RootUtils.runCommand("cp -r $source $dest")
    }

    fun getChecksum(path: String): String {
        return RootUtils.runAndGetOutput("sha1sum $path")
    }

    fun isMagiskBinaryExist(command: String): Boolean {
        return !RootUtils.runAndGetError("/data/adb/magisk/busybox $command")
            .contains("applet not found")
    }

    fun magiskBusyBox(): String {
        return "/data/adb/magisk/busybox"
    }

    fun downloadFile(path: String?, url: String?) {
        /*
         * Based on the following stackoverflow discussion
         * Ref: https://stackoverflow.com/questions/15758856/android-how-to-download-file-from-webserver
         */
        try {
            URL(url).openStream().use { input ->
                FileOutputStream(path).use { output ->
                    val data = ByteArray(4096)
                    var count: Int
                    while (input.read(data).also { count = it } != -1) {
                        output.write(data, 0, count)
                    }
                }
            }
        } catch (ignored: Exception) {
        }
    }

    /**
     * Taken and used almost as such from yoinx's Kernel Adiutor Mod (https://github.com/yoinx/kernel_adiutor/)
     */
    @SuppressLint("DefaultLocale")
    fun getDurationBreakdown(millis: Long): String {
        var millis = millis
        val sb = StringBuilder(64)
        if (millis <= 0) {
            sb.append("00 min 00 s")
            return sb.toString()
        }
        val days = TimeUnit.MILLISECONDS.toDays(millis)
        millis -= TimeUnit.DAYS.toMillis(days)
        val hours = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds = TimeUnit.MILLISECONDS.toSeconds(millis)
        if (days > 0) {
            sb.append(days)
            sb.append(" day ")
        }
        if (hours > 0) {
            sb.append(hours)
            sb.append(" hr ")
        }
        sb.append(String.format("%02d", minutes))
        sb.append(" min ")
        sb.append(String.format("%02d", seconds))
        sb.append(" s")
        return sb.toString()
    }

    @get:SuppressLint("SimpleDateFormat")
    val timeStamp: String
        get() = SimpleDateFormat("yyyyMMdd_HH-mm").format(Date())

    fun getScreenDPI(context: Context): Int {
        val dm: DisplayMetrics = context.resources.displayMetrics
        return dm.densityDpi
    }

    fun prepareReboot(): String {
        return "am broadcast android.intent.action.ACTION_SHUTDOWN " + "&&" +
                " sync " + "&&" +
                " echo 3 > /proc/sys/vm/drop_caches " + "&&" +
                " sync " + "&&" +
                " sleep 3 " + "&&" +
                " reboot"
    }

    /**
     * Taken and used almost as such from the following stackoverflow discussion
     * https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
     */
    fun getExtension(string: String?): String {
        return MimeTypeMap.getFileExtensionFromUrl(string)
    }

    fun removeSuffix(s: String?, suffix: String?): String? {
        return if (s != null && suffix != null && s.endsWith(suffix)) {
            s.substring(0, s.length - suffix.length)
        } else s
    }

    fun getOutput(output: List<String?>): String {
        val mData: MutableList<String> = ArrayList()
        for (line in output.toString().substring(1, output.toString().length - 1).replace(
            ", ", "\n"
        ).replace("ui_print", "").split("\\r?\\n").toTypedArray()) {
            if (!line.startsWith("progress")) {
                mData.add(line)
            }
        }
        return mData.toString().substring(1, mData.toString().length - 1).replace(", ", "\n")
            .replace("(?m)^[ \t]*\r?\n".toRegex(), "")
    }

    fun setLanguage(locale: String?, context: Context) {
        val myLocale = Locale(locale)
        val res = context.resources
        val dm: DisplayMetrics = res.displayMetrics
        val conf = res.configuration
        conf.locale = myLocale
        res.updateConfiguration(conf, dm)
    }

    fun hasStoragePM(activity: Context): Boolean {
        return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        } else {
            activity.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && activity.checkSelfPermission(
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED &&
                    Environment.isExternalStorageManager()
        }
    }

    fun requestPM(activity: Activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                100
            )
        } else {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                100
            )
            activity.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
            Toast.makeText(activity, R.string.help_android11_pm, Toast.LENGTH_LONG).show()
        }
    }

    fun requestPM(fragment: Fragment) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
            fragment.requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 100
            )
        } else {
            fragment.requestPermissions(
                arrayOf(
                    Manifest.permission.MANAGE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), 100
            )
            fragment.startActivity(Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION))
            Toast.makeText(fragment.activity, R.string.help_android11_pm, Toast.LENGTH_LONG).show()
        }
    }
}
