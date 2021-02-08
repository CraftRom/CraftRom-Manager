package com.craftrom.manager.fragments.device

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.adapter.RssItemRecyclerViewAdapter
import com.craftrom.manager.utils.Device
import com.craftrom.manager.utils.RSS.RssItem
import com.craftrom.manager.utils.RSS.RssParser
import com.craftrom.manager.utils.root.CheckRoot
import com.craftrom.manager.utils.root.RootUtils
import com.craftrom.manager.utils.storage.isDiskEncrypted
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.URL
import javax.net.ssl.HttpsURLConnection

class DeviceFragment : Fragment() {
    private lateinit var  oem_name: TextView
    private lateinit var disk_status: TextView
    private lateinit var root_status: TextView
    private lateinit var magisk_status: TextView
    private lateinit var android_codename: TextView
    private lateinit var rom: TextView
    private lateinit var android_version: TextView
    private lateinit var fingerprint: TextView

    // TODO: Customize parameters
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    val RSS_FEED_LINK = "https://www.craft-rom.ml/feed.xml"
    var adapter: RssItemRecyclerViewAdapter? = null
    var rssItems = ArrayList<RssItem>()
    var listV: RecyclerView ?= null

    var root: View? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_device, container, false)
        oem_name = root.findViewById(R.id.oem_name)
        disk_status = root.findViewById(R.id.disk_status)
        root_status = root.findViewById(R.id.root_status)
        android_codename = root.findViewById(R.id.android_codename)
        rom = root.findViewById(R.id.rom)
        android_version = root.findViewById(R.id.android_version)
        fingerprint = root.findViewById(R.id.fingerprint)
        listV = root.findViewById(R.id.listV)

        InitUI()
        return root
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    private fun InitUI() {
        oem_name.text = Device.getVendor() + " " + Device.getModel()
        if (RootUtils.rootAccess()){
            rom.text = Device.ROMInfo.instance!!.version
            if (isDiskEncrypted) {
                disk_status.text = getString(R.string.disk_encrypted)
                disk_status.setTextColor(resources.getColor(R.color.colorTrue))
            } else {
                disk_status.text = getString(R.string.disk_not_encrypted)
                disk_status.setTextColor(resources.getColor(R.color.colorFalse))
            }
        } else {
            disk_status.text = getString(R.string.disk_not_permission)
            disk_status.setTextColor(resources.getColor(R.color.colorPermission))
            rom.text = getString(R.string.disk_not_permission)
            rom.setTextColor(resources.getColor(R.color.colorPermission))
        }


        if (CheckRoot.isDeviceRooted) {
            root_status.text = getString(R.string.device_rooted)
            root_status.setTextColor(resources.getColor(R.color.colorTrue))
        } else {
            root_status.text = getString(R.string.device_not_rooted)
            root_status.setTextColor(resources.getColor(R.color.colorFalse))
        }
        android_codename.text  = Device.getDeviceName()
        android_version.text = Device.getVersion()
        fingerprint.text = Device.getFingerprint()

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = RssItemRecyclerViewAdapter(rssItems, listener, activity)
        listV?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        listV?.adapter = adapter

        val url = URL(RSS_FEED_LINK)
        RssFeedFetcher(this).execute(url)
    }

    fun updateRV(rssItemsL: List<RssItem>) {
        if (rssItemsL != null && !rssItemsL.isEmpty()) {
            rssItems.addAll(rssItemsL)
            adapter?.notifyDataSetChanged()
        }
    }

    class RssFeedFetcher(val context: DeviceFragment) : AsyncTask<URL, Void, List<RssItem>>() {
        val reference = WeakReference(context)
        private var stream: InputStream? = null;
        override fun doInBackground(vararg params: URL?): List<RssItem>? {
            val connect = params[0]?.openConnection() as HttpsURLConnection
            connect.readTimeout = 8000
            connect.connectTimeout = 8000
            connect.requestMethod = "GET"
            connect.connect();

            val responseCode: Int = connect.responseCode;
            var rssItems: List<RssItem>? = null
            if (responseCode == 200) {
                stream = connect.inputStream;


                try {
                    val parser = RssParser()
                    rssItems = parser.parse(stream!!)

                } catch (e: IOException) {
                    e.printStackTrace()
                }


            }

            return rssItems

        }


        override fun onPostExecute(result: List<RssItem>?) {
            super.onPostExecute(result)
            if (result != null && !result.isEmpty()) {
                reference.get()?.updateRV(result)
            }

        }

    }


    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: RssItem?)
    }
}