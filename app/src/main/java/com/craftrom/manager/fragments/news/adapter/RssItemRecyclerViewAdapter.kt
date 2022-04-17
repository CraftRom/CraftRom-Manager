package com.craftrom.manager.fragments.news.adapter

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.activities.WebViewActivity
import com.craftrom.manager.fragments.news.NewsFragment.Companion.LIST_LIMIT
import com.craftrom.manager.utils.Constants
import com.craftrom.manager.utils.Constants.Companion.share
import com.craftrom.manager.utils.rss.RssItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

class RssItemRecyclerViewAdapter(
    private val mValues: List<RssItem>,
    private val context: FragmentActivity?
) : RecyclerView.Adapter<RssItemRecyclerViewAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_news_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]
        val postDate = item.pubDate
        val sim = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault())
        val pDate: Date? = sim.parse(postDate)
        val form = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())

        val date = form.format(pDate)

        holder.titleTV?.text = item.title
        holder.descTV?.text = item.description
        holder.pubDateTV?.text = "$date \u2022 ${item.author}"
        holder.shareBut.setOnClickListener{
            share(context!!, item.title, item.link)
        }

        try {
            val cDate = Date().time.toString()
            val tsp = pDate?.time
            val old = (tsp?.minus (1000 * 60 * 60 * 24 * 30)).toString()
            Log.i(Constants.TAG, "TIMESTAMP POST:\n Curent time: $cDate \n Post time: $tsp \n Diff: $old")
            if (cDate < old) {
                holder.new_icon.visibility = View.VISIBLE
            } else {
                holder.new_icon.visibility = View.GONE
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }

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
        holder.butLink.setOnClickListener {
            // on below line we are creating a new bottom sheet dialog.
            val dialog = BottomSheetDialog(context!!, R.style.ThemeBottomSheet)

            // on below line we are inflating a layout file which we have created.
            val card = LayoutInflater.from(context).inflate(R.layout.dialog_news, null)

            val btnClose = card.findViewById<Button>(R.id.butLink)
            val articleImage =card.findViewById<ImageView>(R.id.featuredImg)
            val titleTxt =card.findViewById<TextView>(R.id.txtTitle)
            val descriptionTxt =card.findViewById<TextView>(R.id.txtContent)
            val dateTxt =card.findViewById<TextView>(R.id.txtPubdate)
            val new_ic =card.findViewById<ImageView>(R.id.new_news)

            try {
                val cDate = Date().time.toString()
                val tsp = pDate?.time
                val old = (tsp?.minus (1000 * 60 * 60 * 24 * 30)).toString()
                if (cDate < old) {
                    new_ic.visibility = View.VISIBLE
                } else {
                    new_ic.visibility = View.GONE
                }
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            titleTxt.text = item.title
            descriptionTxt.text  = item.description
            dateTxt.text = "$date \u2022 ${item.author}"
            holder.shareBut.setOnClickListener{
                share(context, item.title, item.link)
            }
            Picasso.get()
                .load(item.image)
                .fit()
                .centerCrop()
                .into(articleImage)
            // on below line we are adding on click listener
            // for our dismissing the dialog button.
            btnClose.setOnClickListener {
                    val intent = Intent(context, WebViewActivity::class.java)
                    val url = item.link
                    intent.putExtra("link", url)
                context.startActivity(intent)
                dialog.dismiss()
            }
            // below line is use to set cancelable to avoid
            // closing of dialog box when clicking on the screen.
            dialog.setCancelable(true)

            // on below line we are setting
            // content view to our view.
            dialog.setContentView(card)
            dialog.dismissWithAnimation = true
            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()

        }
    }

    override fun getItemCount(): Int {
        return min(mValues.size, LIST_LIMIT)
    }
    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val titleTV: TextView? = mView.findViewById(R.id.txtTitle)
        val descTV: TextView? = mView.findViewById(R.id.txtDesc)
        val pubDateTV: TextView? = mView.findViewById(R.id.txtPubdate)
        val featuredImg: ImageView? = mView.findViewById(R.id.featuredImg)
        val butLink: CardView = mView.findViewById(R.id.news_card)
        val shareBut: ImageView = mView.findViewById(R.id.share_fab)
        val new_icon: ImageView = mView.findViewById(R.id.new_news)
    }
}