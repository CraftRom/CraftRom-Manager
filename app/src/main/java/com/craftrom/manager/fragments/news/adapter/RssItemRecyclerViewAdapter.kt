package com.craftrom.manager.fragments.news.adapter

import android.content.Intent
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
import com.craftrom.manager.utils.rss.RssItem
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.squareup.picasso.Picasso

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
        holder.titleTV?.text = item.title
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
        holder.butLink.setOnClickListener {
// on below line we are creating a new bottom sheet dialog.
            val dialog = BottomSheetDialog(context!!)

            // on below line we are inflating a layout file which we have created.
            val card = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_dialog, null)

            val btnClose = card.findViewById<Button>(R.id.butLink)
            val articleImage =card.findViewById<ImageView>(R.id.featuredImg)
            val titleTxt =card.findViewById<TextView>(R.id.txtTitle)
            val authorNameTxt =card.findViewById<TextView>(R.id.txtAuthor)
            val descriptionTxt =card.findViewById<TextView>(R.id.txtContent)
            val dateTxt =card.findViewById<TextView>(R.id.txtPubdate)

            titleTxt.text = item.title
            descriptionTxt.text  = item.description
            dateTxt.text = item.pubDate
            authorNameTxt.text = item.author

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

            // on below line we are calling
            // a show method to display a dialog.
            dialog.show()
        }
    }

    override fun getItemCount(): Int = mValues.size

    inner class ViewHolder(mView: View) : RecyclerView.ViewHolder(mView) {
        val titleTV: TextView? = mView.findViewById(R.id.txtTitle)
        val pubDateTV: TextView? = mView.findViewById(R.id.txtPubdate)
        val pubAuthorTV: TextView? = mView.findViewById(R.id.txtAuthor)
        val featuredImg: ImageView? = mView.findViewById(R.id.featuredImg)
        val butLink: CardView= mView.findViewById(R.id.news_card)
    }
}