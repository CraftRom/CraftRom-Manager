package com.craftrom.manager.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.fragments.device.DeviceFragment
import com.craftrom.manager.utils.RSS.RssItem
import com.squareup.picasso.Picasso

class RssItemRecyclerViewAdapter(
    private val mValues: List<RssItem>,
    private val mListener: DeviceFragment.OnListFragmentInteractionListener?,
    private val context: FragmentActivity?
) : RecyclerView.Adapter<RssItemRecyclerViewAdapter.ViewHolder>() {


    private val mOnClickListener: View.OnClickListener = View.OnClickListener { v ->
        // val item = v.tag as DummyContent.DummyItem
       // Notify the active callbacks interface (the activity, if the fragment is attached to
        // one) that an item has been selected.
        // mListener?.onListFragmentInteraction(item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.titleTV?.text = item.title
        holder.linkTV?.text = item.link
        holder.contentTV?.text  = item.description
        holder.pubDateTV?.text = item.pubDate



        if(item.image != null) {
            Picasso.get()
                .load(item.image)
                .fit()
                .centerCrop()
                .into(holder.featuredImg)
        }

        with(holder.mView) {
            tag = item
            setOnClickListener(mOnClickListener)
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val titleTV: TextView? = mView.findViewById(R.id.txtTitle)
        val linkTV: TextView? = mView.findViewById(R.id.txtLink)
        val contentTV: TextView? = mView.findViewById(R.id.txtContent)
        val pubDateTV: TextView? = mView.findViewById(R.id.txtPubdate)
        val featuredImg: ImageView? = mView.findViewById(R.id.featuredImg);
    }


    internal interface OnItemClickListener {
        fun onItemSelected(rssItem: RssItem)
    }
}