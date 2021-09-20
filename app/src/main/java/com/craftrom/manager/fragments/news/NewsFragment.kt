package com.craftrom.manager.fragments.news

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.craftrom.manager.R
import com.craftrom.manager.adapter.RssItemRecyclerViewAdapter
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.Constants.Companion.RSS_FEED_LINK
import com.craftrom.manager.utils.RSS.RssFeedFetcher
import com.craftrom.manager.utils.RSS.RssItem
import com.craftrom.manager.utils.dummy.DummyContent
import java.net.URL


class NewsFragment : Fragment(){

    // TODO: Customize parameters
    lateinit var swipeContainer: SwipeRefreshLayout
    private var columnCount = 1
    private var listener: OnListFragmentInteractionListener? = null

    var adapter: RssItemRecyclerViewAdapter? = null
    var rssItems = ArrayList<RssItem>()
    var listV: RecyclerView ?= null

    var root: View? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_news, container, false)
        // Lookup the swipe container view
        swipeContainer = root.findViewById(R.id.swipeContainer)
        swipeContainer.setOnRefreshListener {
            // Your code to refresh the list here.
            // Make sure you call swipeContainer.setRefreshing(false)
            // once the network request has completed successfully.
            val url = URL(RSS_FEED_LINK)
            rssItems.clear()
            adapter!!.notifyDataSetChanged()
            RssFeedFetcher(this).execute(url)
            swipeContainer.isRefreshing = false
        }

        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.blue_500,
            R.color.colorTrue,
            R.color.colorPermission,
            R.color.colorFalse)

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
        adapter = RssItemRecyclerViewAdapter(rssItems, listener, activity)
        listV?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        listV?.adapter = adapter
        adapter!!.notifyDataSetChanged()

        val url = URL(RSS_FEED_LINK)
        RssFeedFetcher(this).execute(url)
        Log.d(Constants.TAG, "NewsFragment: " + RssFeedFetcher(this).execute(url))


    }

    fun updateRV(rssItemsL: List<RssItem>) {

        if (rssItemsL.isNotEmpty()) {
            rssItems.clear()
            rssItems.addAll(rssItemsL)
            adapter!!.notifyDataSetChanged()
            swipeContainer.isRefreshing = false
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
            NewsFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}