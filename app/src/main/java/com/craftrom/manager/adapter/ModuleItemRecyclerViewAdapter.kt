package com.craftrom.manager.adapter

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.fragments.modules.ModuleFragment
import com.craftrom.manager.utils.RSS.RssItem
import com.craftrom.manager.utils.module.ModuleItem

class ModuleItemRecyclerViewAdapter(
    private val mValues: List<ModuleItem>,
    private val mListener: ModuleFragment.OnListFragmentInteractionListener?,
    private val context: FragmentActivity?
) : RecyclerView.Adapter<ModuleItemRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_module_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        holder.titleTV?.text = item.title
        holder.contentTV?.text  = item.description
        holder.pubDateTV?.text = item.pubDate
        holder.pubAuthorTV?.text = context!!.getString(R.string.module_version_author, item.version, item.author)

//        holder.mView.setOnClickListener { _ ->
//
//            Toast.makeText(
//                context, "Вы нажали на кнопку",
//                Toast.LENGTH_SHORT).show()
//        }
        holder.mView.setOnClickListener { _ ->
            val url = item.link
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            context.startActivity(i)

            Toast.makeText(
                    context, R.string.open_link,
                    Toast.LENGTH_SHORT).show()
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(val mView: View) : RecyclerView.ViewHolder(mView) {
        val titleTV: TextView? = mView.findViewById(R.id.module_title)
        val contentTV: TextView? = mView.findViewById(R.id.module_description)
        val pubDateTV: TextView? = mView.findViewById(R.id.module_pubDate)
        val pubAuthorTV: TextView? = mView.findViewById(R.id.module_version_author)
        val butLink: Button? = mView.findViewById(R.id.butLink)
    }


    internal interface OnItemClickListener {
        fun onItemSelected(rssItem: RssItem)
    }
}