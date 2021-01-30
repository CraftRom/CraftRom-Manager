package com.craftrom.manager.utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.ActivityManager
import android.app.UiModeManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.database.Cursor
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.text.Html
import android.util.Base64
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import com.craftrom.manager.BuildConfig
import com.craftrom.manager.utils.root.RootFile
import com.craftrom.manager.utils.root.RootUtils.runAndGetError
import com.craftrom.manager.utils.root.RootUtils.runAndGetOutput
import com.craftrom.manager.utils.root.RootUtils.runCommand
import com.google.android.material.snackbar.Snackbar
import java.io.*
import java.math.BigInteger
import java.net.URL
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * Created by willi on 14.04.16.
 */
object FileUtils {
    var mAbout: Boolean = false
    var mBattery: Boolean = false
    var mDevice: Boolean = false
    var mHasBusybox: Boolean = false
    var mHasRoot: Boolean = false
    var mMemory: Boolean = false
    var mDetailsTitle: String? = null
    var mDetailsTxt: String? = null
    private val TAG: String = FileUtils::class.java.simpleName
    fun isPackageInstalled(id: String?, context: Context): Boolean {
        try {
            if (id != null) {
                context.getPackageManager().getApplicationInfo(id, 0)
            }
            return true
        } catch (ignored: PackageManager.NameNotFoundException) {
            return false
        }
    }

    fun isSPDonated(context: Context): Boolean {
        return isPackageInstalled("com.smartpack.donate", context)
    }


    fun startService(context: Context, intent: Intent?) {
        if (!Prefs.getBoolean("enable_onboot", true, context)) {
            return
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    fun upperCaseEachWord(text: String): String {
        val chars: CharArray = text.toCharArray()
        for (i in chars.indices) {
            if (i == 0) {
                chars[i] = Character.toUpperCase(chars.get(0))
            } else if (Character.isWhitespace(chars.get(i))) {
                chars[i] = Character.toUpperCase(chars.get(i))
            }
        }
        return String(chars)
    }

    fun isTv(context: Context): Boolean {
        return (Objects.requireNonNull(context.getSystemService(Context.UI_MODE_SERVICE)) as UiModeManager)
                .currentModeType == Configuration.UI_MODE_TYPE_TELEVISION
    }

    fun isServiceRunning(serviceClass: Class<*>, context: Context): Boolean {
        val manager: ActivityManager? = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        assert(manager != null)
        for (service: ActivityManager.RunningServiceInfo in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if ((serviceClass.name == service.service.className)) {
                return true
            }
        }
        return false
    }

    fun decodeString(text: String?): String {
        return String(Base64.decode(text, Base64.DEFAULT), StandardCharsets.UTF_8)
    }

    fun htmlFrom(text: String?): CharSequence {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            return Html.fromHtml(text)
        }
    }

    fun getPath(uri: Uri?, context: Context): String? {
        var path: String? = null
        val filePathColumn: Array<String> = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = context.getContentResolver().query(uri!!, filePathColumn, null, null, null)
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndex(filePathColumn.get(0))
                path = cursor.getString(columnIndex)
            }
            cursor.close()
        }
        return path
    }

    val internalDataStorage: String
        get() = Environment.getExternalStorageDirectory().toString().toString() + "/SP"

    fun prepareInternalDataStorage() {
        val file: File = File(internalDataStorage)
        if (file.exists() && file.isFile) {
            file.delete()
        }
        file.mkdirs()
    }

    // MD5 code from
    // https://github.com/CyanogenMod/android_packages_apps_CMUpdater/blob/cm-12.1/src/com/cyanogenmod/updater/utils/MD5.java
    fun checkMD5(md5: String?, updateFile: File?): Boolean {
        if ((md5 == null) || (updateFile == null) || md5.isEmpty()) {
            Log.e(TAG, "MD5 string empty or updateFile null")
            return false
        }
        val calculatedDigest: String? = calculateMD5(updateFile)
        if (calculatedDigest == null) {
            Log.e(TAG, "calculatedDigest null")
            return false
        }
        return calculatedDigest.equals(md5, ignoreCase = true)
    }

    private fun calculateMD5(updateFile: File): String? {
        val digest: MessageDigest
        try {
            digest = MessageDigest.getInstance("MD5")
        } catch (e: NoSuchAlgorithmException) {
            Log.e(TAG, "Exception while getting digest", e)
            return null
        }
        val `is`: InputStream
        try {
            `is` = FileInputStream(updateFile)
        } catch (e: FileNotFoundException) {
            Log.e(TAG, "Exception while getting FileInputStream", e)
            return null
        }
        val buffer: ByteArray = ByteArray(8192)
        var read: Int
        try {
            while ((`is`.read(buffer).also { read = it }) > 0) {
                digest.update(buffer, 0, read)
            }
            val md5sum: ByteArray = digest.digest()
            val bigInt: BigInteger = BigInteger(1, md5sum)
            var output: String? = bigInt.toString(16)
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0')
            return output
        } catch (e: IOException) {
            throw RuntimeException("Unable to process file for MD5", e)
        } finally {
            try {
                `is`.close()
            } catch (e: IOException) {
                Log.e(TAG, "Exception on closing MD5 input stream", e)
            }
        }
    }

    fun isTablet(context: Context): Boolean {
        return ((context.getResources().getConfiguration().screenLayout and Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE)
    }

    fun readAssetFile(context: Context, file: String): String? {
        var input: InputStream? = null
        var buf: BufferedReader? = null
        try {
            val s: StringBuilder = StringBuilder()
            input = context.getAssets().open(file)
            buf = BufferedReader(InputStreamReader(input))
            var str: String?
            while ((buf.readLine().also { str = it }) != null) {
                s.append(str).append("\n")
            }
            return s.toString().trim { it <= ' ' }
        } catch (e: IOException) {
            Log.e(TAG, "Unable to read $file")
        } finally {
            try {
                if (input != null) input.close()
                if (buf != null) buf.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }



    fun celsiusToFahrenheit(celsius: Double): Double {
        return (9.0 / 5.0) * celsius + 32
    }


    fun strFormat(text: String?, vararg format: Any?): String {
        return String.format((text)!!, *format)
    }

    fun strToFloat(text: String): Float {
        try {
            return text.toFloat()
        } catch (ignored: NumberFormatException) {
            return 0f
        }
    }

    fun strToLong(text: String): Long {
        try {
            return text.toLong()
        } catch (ignored: NumberFormatException) {
            return 0L
        }
    }

    fun strToInt(text: String): Int {
        try {
            return text.toInt()
        } catch (ignored: NumberFormatException) {
            return 0
        }
    }

    fun sToString(tSec: Long): String {
        val h: Int = (tSec / (60 * 60)).toInt()
        val m: Int = (tSec.toInt() % (60 * 60)) / 60
        val s: Int = (tSec.toInt() % (60 * 60)) % 60
        var sDur: String = ""
        if (h != 0) sDur = h.toString() + "h "
        if (m != 0) sDur += m.toString() + "m "
        sDur += s.toString() + "s"
        return sDur
    }

    fun isRTL(view: View?): Boolean {
        return ViewCompat.getLayoutDirection(view!!) == ViewCompat.LAYOUT_DIRECTION_RTL
    }

    fun toast(message: String?, context: Context?) {
        toast(message, context, Toast.LENGTH_SHORT)
    }

    fun toast(@StringRes id: Int, context: Context) {
        toast(context.getString(id), context)
    }

    fun toast(@StringRes id: Int, context: Context, duration: Int) {
        toast(context.getString(id), context, duration)
    }

    fun toast(message: String?, context: Context?, duration: Int) {
        Toast.makeText(context, message, duration).show()
    }

    fun snackbar(view: View?, message: String?) {
        val snackBar: Snackbar = Snackbar.make(view!!, (message)!!, Snackbar.LENGTH_LONG)
 //       snackBar.setAction(R.string.dismiss) { v -> snackBar.dismiss() }
        snackBar.show()
    }

    fun launchUrl(view: View?, url: String?, context: Context) {
        if (!isNetworkAvailable(context)) {
//            snackbar(view, context.getString(R.string.no_internet))
            return
        }
        try {
            val i: Intent = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
        }
    }

    fun shareItem(context: Context, name: String?, path: String?, string: String?) {
        val uriFile: Uri = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider", File(path))
        val shareScript: Intent = Intent(Intent.ACTION_SEND)
        shareScript.type = "*/*"
     //   shareScript.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.share_by, name))
        shareScript.putExtra(Intent.EXTRA_TEXT, string)
        shareScript.putExtra(Intent.EXTRA_STREAM, uriFile)
        shareScript.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    //    context.startActivity(Intent.createChooser(shareScript, context.getString(R.string.share_with)))
    }

    fun getOrientation(activity: Activity): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && activity.isInMultiWindowMode) Configuration.ORIENTATION_PORTRAIT else activity.resources.configuration.orientation
    }

    fun isPropRunning(key: String): Boolean {
        try {
            return runAndGetOutput("getprop | grep $key").split("]:".toRegex()).toTypedArray().get(1).contains("running")
        } catch (ignored: Exception) {
            return false
        }
    }

    fun hasProp(key: String): Boolean {
        try {
            return !runAndGetOutput("getprop | grep $key").isEmpty()
        } catch (ignored: Exception) {
            return false
        }
    }

    fun writeFile(path: String, text: String?, append: Boolean, asRoot: Boolean) {
        if (asRoot) {
            RootFile(path).write((text)!!, append)
            return
        }
        var writer: FileWriter? = null
        try {
            writer = FileWriter(path, append)
            writer.write(text)
            writer.flush()
        } catch (e: IOException) {
            Log.e(TAG, "Failed to write $path")
        } finally {
            try {
                if (writer != null) writer.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    @JvmOverloads
    fun readFile(file: String?, root: Boolean = true): String? {
        if (root) {
            return RootFile((file)!!).readFile()
        }
        var buf: BufferedReader? = null
        try {
            buf = BufferedReader(FileReader(file))
            val stringBuilder: StringBuilder = StringBuilder()
            var line: String?
            while ((buf.readLine().also { line = it }) != null) {
                stringBuilder.append(line).append("\n")
            }
            return stringBuilder.toString().trim { it <= ' ' }
        } catch (ignored: IOException) {
        } finally {
            try {
                if (buf != null) buf.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return null
    }

    @JvmOverloads
    fun existFile(file: String?, root: Boolean = true): Boolean {
        return if (!root) File(file).exists() else RootFile((file)!!).exists()
    }

    fun create(text: String?, path: String?) {
        try {
            val logFile: File = File(path)
            logFile.createNewFile()
            val fOut: FileOutputStream = FileOutputStream(logFile)
            val myOutWriter: OutputStreamWriter = OutputStreamWriter(fOut)
            myOutWriter.append(text)
            myOutWriter.close()
            fOut.close()
        } catch (ignored: Exception) {
        }
    }

    fun append(text: String, path: String) {
        runCommand("echo '$text' >> $path")
    }

    fun delete(path: String?) {
        RootFile((path)!!).delete()
    }

    fun sleep(sec: Int) {
        runCommand("sleep $sec")
    }

    fun copy(source: String, dest: String) {
        runCommand("cp -r $source $dest")
    }

    fun getChecksum(path: String): String {
        return runAndGetOutput("sha1sum $path")
    }

    val isDownloadBinaries: Boolean
        get() = (isMagiskBinaryExist("wget") || isMagiskBinaryExist("curl") ||
                existFile("/system/bin/curl") || existFile("/system/bin/wget"))

    fun isMagiskBinaryExist(command: String): Boolean {
        return !runAndGetError("/data/adb/magisk/busybox $command").contains("applet not found")
    }

    fun magiskBusyBox(): String {
        return "/data/adb/magisk/busybox"
    }

    fun importTranslation(url: String, activity: Activity) {
        if (!existFile(internalDataStorage + "/strings.xml") && isNetworkAvailable(activity)) {
            downloadFile(internalDataStorage + "/strings.xml",
                    "https://github.com/SmartPack/SmartPack-Kernel-Manager/raw/master/app/src/main/res/$url/strings.xml", activity)
        }
    }

    fun downloadFile(path: String, url: String, context: Context) {
        if (!isNetworkAvailable(context)) {
            toast("No internet", context)
            return
        }
        if (isMagiskBinaryExist("wget")) {
            runCommand(magiskBusyBox() + " wget -O " + path + " " + url)
        } else if (isMagiskBinaryExist("curl")) {
            runCommand(magiskBusyBox() + " curl -L -o " + path + " " + url)
        } else if (isDownloadBinaries) {
            runCommand((if (existFile("/system/bin/curl")) "curl -L -o " else "wget -O ") + path + " " + url)
        } else {
            /*
             * Based on the following stackoverflow discussion
             * Ref: https://stackoverflow.com/questions/15758856/android-how-to-download-file-from-webserver
             */
            try {
                URL(url).openStream().use { input ->
                    FileOutputStream(path).use { output ->
                        val data: ByteArray = ByteArray(4096)
                        var count: Int
                        while ((input.read(data).also { count = it }) != -1) {
                            output.write(data, 0, count)
                        }
                    }
                }
            } catch (ignored: Exception) {
            }
        }
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val cm: ConnectivityManager? = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        if (BuildConfig.DEBUG && cm == null) {
            error("Assertion failed")
        }
        return (cm!!.activeNetworkInfo != null) && cm.activeNetworkInfo!!.isConnectedOrConnecting
    }

    fun getFilePath(file: File): String {
        var path: String = file.absolutePath
        if (path.startsWith("/document/raw:")) {
            path = path.replace("/document/raw:", "")
        } else if (path.startsWith("/document/primary:")) {
            path = (Environment.getExternalStorageDirectory().toString() + ("/") + path.replace("/document/primary:", ""))
        } else if (path.startsWith("/document/")) {
            path = path.replace("/document/", "/storage/").replace(":", "/")
        }
        if (path.startsWith("/storage_root/storage/emulated/0")) {
            path = path.replace("/storage_root/storage/emulated/0", "/storage/emulated/0")
        } else if (path.startsWith("/storage_root")) {
            path = path.replace("storage_root", "storage/emulated/0")
        }
        if (path.startsWith("/external")) {
            path = path.replace("external", "storage/emulated/0")
        }
        if (path.startsWith("/root/")) {
            path = path.replace("/root", "")
        }
        if (path.contains("file%3A%2F%2F%2F")) {
            path = path.replace("file%3A%2F%2F%2F", "").replace("%2F", "/")
        }
        if (path.contains("%2520")) {
            path = path.replace("%2520", " ")
        }
        return path
    }

    fun isDocumentsUI(uri: Uri): Boolean {
        return ("com.android.providers.downloads.documents" == uri.getAuthority())
    }

    /**
     * Taken and used almost as such from yoinx's Kernel Adiutor Mod (https://github.com/yoinx/kernel_adiutor/)
     */
    @SuppressLint("DefaultLocale")
    fun getDurationBreakdown(millis: Long): String {
        var millis: Long = millis
        val sb: StringBuilder = StringBuilder(64)
        if (millis <= 0) {
            sb.append("00 min 00 s")
            return sb.toString()
        }
        val days: Long = TimeUnit.MILLISECONDS.toDays(millis)
        millis -= TimeUnit.DAYS.toMillis(days)
        val hours: Long = TimeUnit.MILLISECONDS.toHours(millis)
        millis -= TimeUnit.HOURS.toMillis(hours)
        val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millis)
        millis -= TimeUnit.MINUTES.toMillis(minutes)
        val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millis)
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
        get() {
            return SimpleDateFormat("yyyyMMdd_HH-mm").format(Date())
        }

    fun getScreenDPI(context: Context): Int {
        val dm: DisplayMetrics = context.getResources().getDisplayMetrics()
        return dm.densityDpi
    }

    fun prepareReboot(): String {
        val prepareReboot: String = ("am broadcast android.intent.action.ACTION_SHUTDOWN " + "&&" +
                " sync " + "&&" +
                " echo 3 > /proc/sys/vm/drop_caches " + "&&" +
                " sync " + "&&" +
                " sleep 3 " + "&&" +
                " reboot")
        return prepareReboot
    }

    /**
     * Taken and used almost as such from the following stackoverflow discussion
     * https://stackoverflow.com/questions/3571223/how-do-i-get-the-file-extension-of-a-file-in-java
     */
    fun getExtension(string: String?): String {
        return MimeTypeMap.getFileExtensionFromUrl(string)
    }

    fun removeSuffix(s: String?, suffix: String?): String? {
        if ((s != null) && (suffix != null) && s.endsWith(suffix)) {
            return s.substring(0, s.length - suffix.length)
        }
        return s
    }

    fun getOutput(output: List<String?>): String {
        val mData: MutableList<String> = ArrayList()
        for (line: String in output.toString().substring(1, output.toString().length - 1).replace(
                ", ", "\n").replace("ui_print", "").split("\\r?\\n".toRegex()).toTypedArray()) {
            if (!line.startsWith("progress")) {
                mData.add(line)
            }
        }
        return mData.toString().substring(1, mData.toString().length - 1).replace(", ", "\n")
                .replace("(?m)^[ \t]*\r?\n".toRegex(), "")
    }
}