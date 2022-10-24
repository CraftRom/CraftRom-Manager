package com.craftrom.manager.ui.home

import android.annotation.SuppressLint
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.core.ServiceContext.context
import com.craftrom.manager.ui.home.HomeFragment.Companion.LIST_LIMIT
import com.craftrom.manager.ui.view.ItemWebViewActivity
import com.craftrom.manager.utils.Const.TAG
import com.craftrom.manager.utils.Const.getShare
import com.squareup.picasso.Picasso
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min


class AdapterRecyclerViewNews internal constructor(private val arrayListRssContent: ArrayList<RecyclerViewNewsItem>) : RecyclerView.Adapter<AdapterRecyclerViewNews.ViewHolderRV>() {

    private var lastPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderRV {

        val layoutInflater = LayoutInflater.from(parent.context)
        val viewInflated = layoutInflater.inflate(R.layout.item_news_layout, parent, false)


        return ViewHolderRV(viewInflated)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolderRV, position: Int) {

        val (description, image, author, link, publishDate, title) = arrayListRssContent[position]
        val postDate = publishDate?:""
        val sim = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault())
        val pDate: Date = sim.parse(postDate) as Date
        val form = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
        val date = form.format(pDate)
        holder.tvFeedTitle.text = title?:""
        val authorPub = author?:""
        Log.d("loadAuthor", authorPub)

        Picasso.get()
            .load(image?:"")
            .fit()
            .centerCrop()
            .into(holder.ivFeedImage)

        try {
            val cDate = Date().time.toString()
            val tsp = pDate.time
            val old = (tsp.minus (1000 * 60 * 60 * 24 * 30)).toString()
            Log.i(TAG, "TIMESTAMP POST:\n Current time: $cDate \n Post time: $tsp \n Diff: $old")
            if (cDate < old) {
                holder.new_icon.visibility = View.VISIBLE
            } else {
                holder.new_icon.visibility = View.GONE
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
        setAnimation(holder.itemView)
        holder.tvFeedPublishDate.text = "$date \u2022 $authorPub"
        holder.tvFeedDescription.text = description?:""

        holder.shareBut.setOnClickListener{
            val desc = description?:""

            val body = "${title?:""}\n\n$desc\nby $authorPub\n\n${link?:""}"
            getShare(context,  body)
        }

        holder.butLink.setOnClickListener {
            val intent = Intent(context, ItemWebViewActivity::class.java)
            intent.putExtra("feedItemUrl", link?:"")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int = min(arrayListRssContent.size, LIST_LIMIT)

    inner class ViewHolderRV(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvFeedTitle: TextView = itemView.findViewById(R.id.txtTitle)
        val ivFeedImage: ImageView = itemView.findViewById(R.id.featuredImg)
        val tvFeedPublishDate: TextView = itemView.findViewById(R.id.txtPubdate)
        val tvFeedDescription: TextView = itemView.findViewById(R.id.txtDesc)
        val butLink: CardView = itemView.findViewById(R.id.news_card)
        val shareBut: ImageView = itemView.findViewById(R.id.share_fab)
        val new_icon: ImageView = itemView.findViewById(R.id.new_news)
    }
    /**
     * Here is the key method to apply the animation
     */
    private fun setAnimation(view: View) {
        // If the bound view wasn't previously displayed on screen, it's animated
        val animation = AnimationUtils.loadAnimation(context, R.anim.recyclerview) // animation file
        view.startAnimation(animation)
    }
}
