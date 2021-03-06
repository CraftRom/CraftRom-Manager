package com.craftrom.manager.fragments.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.adapter.ModuleItemRecyclerViewAdapter
import com.craftrom.manager.fragments.device.DeviceFragment
import com.craftrom.manager.utils.dummy.DummyContent
import com.craftrom.manager.utils.module.ModuleFeedFetcher
import com.craftrom.manager.utils.module.ModuleItem
import java.net.URL

class ModuleFragment : Fragment() {

    // TODO: Customize parameters
    private var columnCount = 1
    private var listener: ModuleFragment.OnListFragmentInteractionListener? = null
    private val RSS_FEED_LINK = "https://raw.githubusercontent.com/CraftRom/KernelUpdates/android-10/modules.xml"
    var adapter: ModuleItemRecyclerViewAdapter? = null
    var moduleItems = ArrayList<ModuleItem>()
    var listV: RecyclerView?= null

    var root: View? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_module, container, false)
        listV = root.findViewById(R.id.listV)
        arguments?.let {
            columnCount = it.getInt(DeviceFragment.ARG_COLUMN_COUNT)
        }
        return root
    }
    @Suppress("DEPRECATION")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        adapter = ModuleItemRecyclerViewAdapter(moduleItems, listener, activity)
        listV?.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        listV?.adapter = adapter
        adapter!!.notifyDataSetChanged()

        val url = URL(RSS_FEED_LINK)
        ModuleFeedFetcher(this).execute(url)


    }

    fun updateRV(rssItemsL: List<ModuleItem>) {
        if (rssItemsL.isNotEmpty()) {
            moduleItems.clear()
            moduleItems.addAll(rssItemsL)
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