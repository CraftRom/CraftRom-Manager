package com.craftrom.manager.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.craftrom.manager.R
import com.craftrom.manager.utils.module.ModuleItem
import com.topjohnwu.superuser.internal.Utils.context

class ModuleItemRecyclerViewAdapter(context: Context,arrayListDetails:ArrayList<ModuleItem>)  : BaseAdapter(){

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)
    private val arrayListDetails:ArrayList<ModuleItem>

    init {
        this.arrayListDetails=arrayListDetails
    }

    override fun getCount(): Int {
        return arrayListDetails.size
    }

    override fun getItem(position: Int): Any {
        return arrayListDetails[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View? {
        val view: View?
        val listRowHolder: ListRowHolder
        if (convertView == null) {
            view = this.layoutInflater.inflate(R.layout.item_module_layout, parent, false)
            listRowHolder = ListRowHolder(view)
            view.tag = listRowHolder
        } else {
            view = convertView
            listRowHolder = view.tag as ListRowHolder
        }

        listRowHolder.tvTitle.text = arrayListDetails[position].title
        listRowHolder.tvDescription.text = arrayListDetails[position].description
        listRowHolder.tvPubDate.text = arrayListDetails[position].pubDate
        listRowHolder.tvAuthor.text = arrayListDetails[position].author
        listRowHolder.butLink!!.setOnClickListener {
            val url = arrayListDetails[position].link
            val i = Intent(Intent.ACTION_VIEW)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            i.data = Uri.parse(url)
            context.startActivity(i)

            Toast.makeText(
               context, R.string.open_link,
               Toast.LENGTH_SHORT).show()
     }

        return view
    }
}

private class ListRowHolder(row: View?) {
    val tvPubDate: TextView = row!!.findViewById(R.id.txtPubdate) as TextView
    val tvTitle: TextView = row!!.findViewById(R.id.txtTitle) as TextView
    val tvDescription: TextView = row!!.findViewById(R.id.txtContent) as TextView
    val tvAuthor: TextView = row!!.findViewById(R.id.txtAuthor) as TextView
    val butLink: Button? = row!!.findViewById(R.id.butLink)

}