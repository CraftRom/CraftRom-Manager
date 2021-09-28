package com.craftrom.manager.fragments.rom

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.craftrom.manager.R
import com.craftrom.manager.adapter.RomItemRecyclerViewAdapter
import com.craftrom.manager.fragments.device.DeviceFragment
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.dummy.DummyContent
import com.craftrom.manager.utils.rom.RomFeedFetcher
import com.craftrom.manager.utils.rom.RomItem
import java.net.URL

class RomFragment : Fragment() {

    // TODO: Customize parameters
    lateinit var swipeContainer: SwipeRefreshLayout
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null
    private val RSS_FEED_LINK = "https://raw.githubusercontent.com/CraftRom/host_updater/android-10/rom.xml"
    var adapter: RomItemRecyclerViewAdapter? = null
    var romItems = ArrayList<RomItem>()
    var listV: RecyclerView?= null

    var root: View? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rom, container, false)
        // Lookup the swipe container view
        swipeContainer = root.findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            val url = URL(RSS_FEED_LINK)
            romItems.clear()
            adapter!!.notifyDataSetChanged()
            if(isNetworkAvailable(context)){
                RomFeedFetcher(this).execute(url)
                Log.d(Constants.TAG, "NewsFragment: " + RomFeedFetcher(this).execute(url))
            }
            else{
                Toast.makeText(
                    context, R.string.network_unavailable,
                    Toast.LENGTH_SHORT).show()
            }
            swipeContainer.isRefreshing = false
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.blue_500,
            R.color.colorTrue,
            R.color.colorPermission,
            R.color.colorFalse)

        listV = root.findViewById(R.id.listV)
        arguments?.let {
            columnCount = it.getInt(DeviceFragment.ARG_COLUMN_COUNT)
        }
        return root
    }
    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeContainer.isRefreshing = true
        adapter = RomItemRecyclerViewAdapter(romItems, listener, activity)
        listV?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        listV?.adapter = adapter
        adapter!!.notifyDataSetChanged()

        if(isNetworkAvailable(context)){
            val url = URL(Constants.RSS_FEED_LINK)
            RomFeedFetcher(this).execute(url)
            Log.d(Constants.TAG, "NewsFragment: " + RomFeedFetcher(this).execute(url))
        }
        else{
            Toast.makeText(
                context, R.string.network_unavailable,
                Toast.LENGTH_SHORT).show()
            swipeContainer.isRefreshing = false
        }


    }

    fun updateRV(rssItemsL: List<RomItem>) {
        if (rssItemsL.isNotEmpty()) {
            romItems.clear()
            romItems.addAll(rssItemsL)
            adapter?.notifyDataSetChanged()
            swipeContainer.isRefreshing = false
        }
    }

    fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
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