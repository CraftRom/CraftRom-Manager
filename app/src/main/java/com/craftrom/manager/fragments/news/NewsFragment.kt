package com.craftrom.manager.fragments.news

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.craftrom.manager.R
import com.craftrom.manager.fragments.news.adapter.RssItemRecyclerViewAdapter
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.Constants.Companion.RSS_FEED_LINK
import com.craftrom.manager.utils.Constants.Companion.showSnackMessage
import com.craftrom.manager.utils.rss.RssFeedFetcher
import com.craftrom.manager.utils.rss.RssItem
import java.net.URL


class NewsFragment : Fragment(){

    // TODO: Customize parameters
    lateinit var swipeContainer: SwipeRefreshLayout
    lateinit var addMaterialContainer: ConstraintLayout
    lateinit var netError: LinearLayout
    lateinit var  btnRefresh: Button
    private var columnCount = 1

    var adapter: RssItemRecyclerViewAdapter? = null
    var rssItems = ArrayList<RssItem>()
    var listV: RecyclerView ?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_news, container, false)
        // Lookup the swipe container view
        netError = root.findViewById(R.id.errorInternet)
        btnRefresh = root.findViewById(R.id.btnRefresh)
        swipeContainer = root.findViewById(R.id.swipeContainer)
        addMaterialContainer = root.findViewById(R.id.addMaterialContainer)
        swipeContainer.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            val url = URL(RSS_FEED_LINK)
            rssItems.clear()
            adapter!!.notifyDataSetChanged()
            if(isNetworkAvailable(context)){
                RssFeedFetcher(this).execute(url)
                Log.d(Constants.TAG, "NewsFragment: " + RssFeedFetcher(this).execute(url))
                swipeContainer.visibility = View.VISIBLE
                netError.visibility = View.GONE
            }
            else{
                swipeContainer.visibility = View.GONE
                netError.visibility = View.VISIBLE
                showSnackMessage(addMaterialContainer,
                    getString(R.string.network_unavailable)
                )
            }
            swipeContainer.isRefreshing = false
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.blue_500,
            R.color.colorTrue,
            R.color.colorPermission,
            R.color.colorFalse)

        btnRefresh.setOnClickListener {
            val url = URL(RSS_FEED_LINK)
            rssItems.clear()
            adapter!!.notifyDataSetChanged()
            if(isNetworkAvailable(context)){
                RssFeedFetcher(this).execute(url)
                Log.d(Constants.TAG, "NewsFragment: " + RssFeedFetcher(this).execute(url))
                swipeContainer.visibility = View.VISIBLE
                netError.visibility = View.GONE
            }
            else{
                swipeContainer.visibility = View.GONE
                netError.visibility = View.VISIBLE
                showSnackMessage(addMaterialContainer,
                    getString(R.string.network_unavailable)
                )
            }
        }
        listV = root.findViewById(R.id.listV)
        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        return root
    }

    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        swipeContainer.isRefreshing = true
        adapter = RssItemRecyclerViewAdapter(rssItems, activity)
        listV?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        listV?.adapter = adapter
        adapter!!.notifyDataSetChanged()
        if(isNetworkAvailable(context)){
            swipeContainer.visibility = View.VISIBLE
            netError.visibility = View.GONE
            val url = URL(RSS_FEED_LINK)
            RssFeedFetcher(this).execute(url)
            Log.d(Constants.TAG, "NewsFragment: " + RssFeedFetcher(this).execute(url))
        }
        else{
            swipeContainer.visibility = View.GONE
            netError.visibility = View.VISIBLE
            showSnackMessage(addMaterialContainer,
                getString(R.string.network_unavailable)
            )
            swipeContainer.isRefreshing = false
        }
    }

    fun updateRV(rssItemsL: List<RssItem>) {

        if (rssItemsL.isNotEmpty()) {
            rssItems.clear()
            rssItems.addAll(rssItemsL)
            adapter!!.notifyDataSetChanged()
            swipeContainer.isRefreshing = false
        } else
        {
            Log.e(Constants.TAG, "NewsFragment: ERRORS")
        }
    }

    private fun isNetworkAvailable(context: Context?): Boolean {
        if (context == null) return false
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
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
        return false
    }

    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}