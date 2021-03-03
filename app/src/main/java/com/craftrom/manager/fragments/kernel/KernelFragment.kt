package com.craftrom.manager.fragments.kernel

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.Device
import com.craftrom.manager.utils.notification.NotificationUtils
import com.craftrom.manager.utils.root.RootUtils
import com.onesignal.OneSignal
import com.topjohnwu.superuser.internal.Utils
import io.github.inflationx.calligraphy3.CalligraphyConfig
import io.github.inflationx.calligraphy3.CalligraphyInterceptor
import io.github.inflationx.viewpump.ViewPump
import kotlinx.android.synthetic.main.activity_intro_final.*
import kotlinx.android.synthetic.main.fragment_kernel.*
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*


@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
class KernelFragment : Fragment() {

    private lateinit var  kernel_name: TextView
    lateinit var host: String
    lateinit var device: String
    var sp = Utils.context.getSharedPreferences("com.craftrom.manager", Context.MODE_PRIVATE)


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_kernel, container, false)
        kernel_name = root.findViewById(R.id.kernel_name)
        val checkUpdate: CardView = root.findViewById(R.id.kernel_version)
        val kernelVersion = readKernelVersion()
        val buildDate: Date  = SimpleDateFormat("MMM dd HH yyyy", Locale.ENGLISH).parse(
            kernelVersion.substring(
                kernelVersion.lastIndexOf(
                    "PREEMPT"
                )
            ).substring(12, 20) + " ${Constants.CURRENT_YEAR}"
        )
        InitUI()

        checkUpdate.setOnClickListener(View.OnClickListener {
            content.visibility = View.GONE
            loader.visibility = View.VISIBLE

            checkForUpdates(buildDate)
        })

        AndroidNetworking.initialize(context)

        //onesignal init
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.startInit(context)
            .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
            .init()
        OneSignal.clearOneSignalNotifications()

        ViewPump.init(
            ViewPump.builder()
                .addInterceptor(
                    CalligraphyInterceptor(
                        CalligraphyConfig.Builder()
                            .setDefaultFontPath("RobotoMono-Regular.ttf")
                            .setFontAttrId(R.attr.fontPath)
                            .build()
                    )
                ).build()
        )

        AndroidNetworking
            .get(Constants.HOST_REFERENCE)
            .doNotCacheResponse()
            .build()
            .getAsString(object : StringRequestListener {
                override fun onResponse(response: String) {
                    host = response
                    device  = Device.getDeviceName().toString()
                    checkForUpdates(buildDate)
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(
                        context,
                        "Please check your internet connection.",
                        Toast.LENGTH_LONG
                    ).show()
                    finish
                }
            })

        return root
    }

        private fun stopLoading(updateAvailable: Boolean, downloadLink: String = ""){
        loader.visibility = View.GONE
        if(updateAvailable){
            updateStatusImg.setImageDrawable(this.requireContext().getDrawable(R.drawable.crossfix))
            updateStatusTv.text = getString(R.string.new_update)
            updateButton.visibility = View.VISIBLE
            updateButton.setOnClickListener {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(downloadLink)))
            }
        }

        content.visibility = View.VISIBLE
    }

    private fun areStatsStored() = sp.getString("codeName", "rip") != "rip"

    fun checkForUpdates(buildDate: Date){
        AndroidNetworking
                .get("$host/$device.json")
                .doNotCacheResponse()
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    @SuppressLint("SetTextI18n")
                    override fun onResponse(response: JSONObject) {
                        val latestBuidDate: Date = SimpleDateFormat(
                            "E MMM dd HH:mm:ss yyyy",
                            Locale.ENGLISH
                        ).parse(response.getString("builtAt"))
                        val latestDate: Date =
                            SimpleDateFormat("MMM dd HH yyyy", Locale.ENGLISH).parse(
                                response.getString(
                                    "latestDate"
                                ) + " ${Constants.CURRENT_YEAR}"
                            )
                        val latestFDate =
                            SimpleDateFormat("MMM d, HH:mm yyyy", Locale.ENGLISH).format(
                                latestBuidDate
                            )
                        val codeName = response.getString("codeName")
                        val cafTag = response.getString("cafTag")
                        val linuxVersion = response.getString("linuxVersion")
                        val buildFdate = SimpleDateFormat("MMM d, yyyy", Locale.ENGLISH).format(
                            latestDate
                        )
                        val editor = sp.edit()

                        if (latestDate.after(buildDate)) {
                            NotificationUtils.notify(
                                getString(R.string.new_update),
                                getString(R.string.new_update_message) + " Cidori Kernel " + linuxVersion
                            )
                            editor.putString("codeName", codeName)
                            editor.putString("cafTag", cafTag)
                            editor.putString("linuxVersion", linuxVersion)
                            editor.putString("buildDate", latestFDate)
                            editor.apply()

                            codeNameTv.text = codeName
                            cafTagTv.text = cafTag
                            linuxVersionTv.text = linuxVersion
                            buildDateTv.text = buildFdate

                            updateBuildDateTv.text = "built at: $buildFdate"
                            updateBuildDateTv.visibility = View.VISIBLE
                            stopLoading(true, response.getString("downloadLink"))
                        } else {
                            stopLoading(false)
                            editor.putString("codeName", codeName)
                            editor.putString("cafTag", cafTag)
                            editor.putString("linuxVersion", linuxVersion)
                            editor.putString("buildDate", latestFDate)
                            editor.apply()

                            codeNameTv.text = codeName
                            cafTagTv.text = cafTag
                            linuxVersionTv.text = linuxVersion
                            buildDateTv.text = latestFDate
                        }
                    }

                    override fun onError(anError: ANError?) {
                        kernel_version.visibility = View.GONE
                        loader.visibility = View.GONE
                        updateStatusImg.setImageDrawable(context?.getDrawable(R.drawable.warn))
                        updateStatusTv.text = getString(R.string.device_not_support)
                        content.visibility = View.VISIBLE
                        Toast.makeText(
                            context,
                            getString(R.string.device_not_support) + device,
                            Toast.LENGTH_LONG
                        ).show()

                        finish
                       }
                })
    }


    private fun readKernelVersion(): String {
        try {
            val p = Runtime.getRuntime().exec("uname -av")
            val `is`: InputStream? = if (p.waitFor() == 0) {
                p.inputStream
            } else {
                p.errorStream
            }
            val br = BufferedReader(
                InputStreamReader(`is`),
                32
            )
            val line = br.readLine()
            br.close()
            return line
        } catch (ex: Exception) {
            return "ERROR: " + ex.message
        }

    }

    private fun InitUI() {
        val versionString:String
        if (RootUtils.rootAccess()){
            versionString = Device.getKernelVersion(true)
        } else
        {
            versionString = RootUtils.runCommand("uname -av").toString()
        }

        val kernelString = "<b> Kernel: </b>" + versionString

        kernel_name.text = Html.fromHtml(kernelString, Html.FROM_HTML_MODE_LEGACY)

    }
}