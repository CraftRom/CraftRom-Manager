package com.craftrom.manager.fragments.device

import android.annotation.SuppressLint
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
import com.craftrom.manager.utils.RSS.RssFeedFetcher
import com.craftrom.manager.utils.RSS.RssItem
import com.craftrom.manager.utils.dummy.DummyContent
import com.craftrom.manager.utils.rom_checker.RomIdentifier.getRom
import com.craftrom.manager.utils.root.RootUtils
import com.craftrom.manager.utils.storage.isDiskEncrypted
import java.net.URL


class DeviceFragment : Fragment(){
    private lateinit var  oem_name: TextView
    private lateinit var disk_status: TextView
    private lateinit var magisk_status: TextView
    private lateinit var android_codename: TextView
    private lateinit var android_version: TextView
    private lateinit var rom_version: TextView

    // TODO: Customize parameters
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private val RSS_FEED_LINK = "https://www.craft-rom.ml/feed.xml"
    var adapter: RssItemRecyclerViewAdapter? = null
    var rssItems = ArrayList<RssItem>()
    var listV: RecyclerView ?= null

    var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_device, container, false)

        oem_name = root.findViewById(R.id.oem_name)
        disk_status = root.findViewById(R.id.disk_status)
        android_codename = root.findViewById(R.id.android_codename)
        android_version = root.findViewById(R.id.android_version)
        rom_version = root.findViewById(R.id.rom_version)

        listV = root.findViewById(R.id.listV)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        InitUI()
        return root
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    private fun InitUI() {
        val rom = getRom()

        oem_name.text = Device.getVendor() + " " + Device.getModel()
        if (RootUtils.rootAccess()){
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
        }

        android_codename.text  = Device.getDeviceName()
        android_version.text = Device.getVersion()
        rom_version.text = rom.name + " " + rom.versionName

    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = RssItemRecyclerViewAdapter(rssItems, listener, activity)
        listV?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        listV?.adapter = adapter
        adapter!!.notifyDataSetChanged()

        val url = URL(RSS_FEED_LINK)
        RssFeedFetcher(this).execute(url)


    }

    fun updateRV(rssItemsL: List<RssItem>) {
        if (rssItemsL.isNotEmpty()) {
            rssItems.clear()
            rssItems.addAll(rssItemsL)
            adapter?.notifyDataSetChanged()
        }
    }


    interface OnListFragmentInteractionListener {
        fun onListFragmentInteraction(item: DummyContent.DummyItem)
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            DeviceFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}