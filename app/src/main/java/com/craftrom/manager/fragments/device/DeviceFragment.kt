package com.craftrom.manager.fragments.device

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.craftrom.manager.R
import com.craftrom.manager.utils.Device
import com.craftrom.manager.utils.RSS.RssItemsAdapter
import com.craftrom.manager.utils.RSS.RssService
import com.craftrom.manager.utils.RSS.converter.RssConverterFactory
import com.craftrom.manager.utils.RSS.converter.RssFeed
import com.craftrom.manager.utils.RSS.converter.RssItem
import com.craftrom.manager.utils.root.CheckRoot
import com.craftrom.manager.utils.root.RootUtils
import com.craftrom.manager.utils.storage.isDiskEncrypted
import kotlinx.android.synthetic.main.fragment_device.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class DeviceFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener,
RssItemsAdapter.OnItemClickListener {
    private lateinit var  oem_name: TextView
    private lateinit var disk_status: TextView
    private lateinit var root_status: TextView
    private lateinit var magisk_status: TextView
    private lateinit var android_codename: TextView
    private lateinit var rom: TextView
    private lateinit var android_version: TextView
    private lateinit var fingerprint: TextView
    private var feedUrl: String? = null
    private lateinit var mAdapter: RssItemsAdapter
    var root: View? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        feedUrl = arguments?.getString(KEY_FEED)

    }

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

        InitUI()
        return root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = RssItemsAdapter(this)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = mAdapter
        swRefresh.setOnRefreshListener(this)

        fetchRss()
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

    /**
     * Fetches Rss Feed Url
     */
    private fun fetchRss() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://www.craft-rom.ml/")
            .addConverterFactory(RssConverterFactory.create())
            .build()

        showLoading()
        val service = retrofit.create(RssService::class.java)

        feedUrl?.apply {
            service.getRss(this)
                .enqueue(object : Callback<RssFeed> {
                    override fun onResponse(call: Call<RssFeed>, response: Response<RssFeed>) {
                        onRssItemsLoaded(response.body()!!.items!!)
                        hideLoading()
                    }

                    override fun onFailure(call: Call<RssFeed>, t: Throwable) {
                        Toast.makeText(activity, "Failed to fetchRss RSS feed!", Toast.LENGTH_SHORT).show()

                    }
                })
        }
    }

    /**
     * Loads fetched [RssItem] list to RecyclerView
     * @param rssItems
     */
    fun onRssItemsLoaded(rssItems: List<RssItem>) {
        mAdapter.setItems(rssItems)
        mAdapter.notifyDataSetChanged()
        if (recyclerView.visibility != View.VISIBLE) {
            recyclerView.visibility = View.VISIBLE
        }
    }

    /**
     * Shows [SwipeRefreshLayout]
     */
    private fun showLoading() {
        swRefresh.isRefreshing = true
    }


    /**
     * Hides [SwipeRefreshLayout]
     */
    fun hideLoading() {
        swRefresh.isRefreshing = false
    }


    /**
     * Triggers on [SwipeRefreshLayout] refresh
     */
    override fun onRefresh() {
        fetchRss()
    }

    /**
     * Browse [RssItem] url
     * @param rssItem
     */
    override fun onItemSelected(rssItem: RssItem) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(rssItem.link))
        startActivity(intent)
    }

    companion object {
        private const val KEY_FEED = "FEED"

        /**
         * Creates new instance of [RssFragment]
         * @param feedUrl Fetched Url Feed
         * @return Fragment
         */
        fun newInstance(feedUrl: String): DeviceFragment {
            val rssFragment = DeviceFragment()
            val bundle = Bundle()
            bundle.putSerializable(KEY_FEED, feedUrl)
            rssFragment.arguments = bundle
            return rssFragment
        }
    }
}