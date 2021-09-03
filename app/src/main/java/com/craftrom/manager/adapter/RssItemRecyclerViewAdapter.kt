package com.craftrom.manager.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.fragments.news.NewsFragment
import com.craftrom.manager.utils.RSS.RssItem
import com.squareup.picasso.Picasso

class RssItemRecyclerViewAdapter(
    private val mValues: List<RssItem>,
    private val mListener: NewsFragment.OnListFragmentInteractionListener?,
    private val context: FragmentActivity?
) : RecyclerView.Adapter<RssItemRecyclerViewAdapter.ViewHolder>() {


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
        holder.pubAuthorTV?.text = item.author



        Picasso.get()
            .load(item.image)
            .fit()
            .centerCrop()
            .into(holder.featuredImg)

//        holder.mView.setOnClickListener { _ ->
//
//            Toast.makeText(
//                context, "Вы нажали на кнопку",
//                Toast.LENGTH_SHORT).show()
//        }
        holder.butLink?.setOnClickListener { _ ->
            val url = item.link
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context?.startActivity(i)

            Toast.makeText(
                context, R.string.open_link,
                Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val titleTV: TextView? = mView.findViewById(R.id.txtTitle)
        val linkTV: TextView? = mView.findViewById(R.id.txtLink)
        val contentTV: TextView? = mView.findViewById(R.id.txtContent)
        val pubDateTV: TextView? = mView.findViewById(R.id.txtPubdate)
        val pubAuthorTV: TextView? = mView.findViewById(R.id.txtAuthor)
        val featuredImg: ImageView? = mView.findViewById(R.id.featuredImg)
        val butLink: Button? = mView.findViewById(R.id.butLink)
    }


    internal interface OnItemClickListener {
        fun onItemSelected(rssItem: RssItem)
    }
}