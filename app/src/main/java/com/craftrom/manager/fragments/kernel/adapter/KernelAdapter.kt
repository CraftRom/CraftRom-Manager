package com.craftrom.manager.fragments.kernel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.utils.DeviceSystemInfo
import com.craftrom.manager.utils.updater.response.KernelUpdateResponse
import com.google.android.material.bottomsheet.BottomSheetDialog

class KernelAdapter (
    private val context: FragmentActivity?,
    val data: List<KernelUpdateResponse>?) : RecyclerView.Adapter<KernelAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_new_kernel, parent, false)
        return MyHolder(v)
    }

    override fun getItemCount(): Int = data?.size ?: 0

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(data?.get(position))
        val item = data?.get(position)
        if (item?.commit == null){
            holder.comitLine.visibility = View.GONE
            holder.commit.text = DeviceSystemInfo.errorResult()
        } else {
            holder.comitLine.visibility = View.VISIBLE
            holder.commit.text = item.commit
        }
        holder.changelogBTN.setOnClickListener {
                // on below line we are creating a new bottom sheet dialog.
                val dialog = BottomSheetDialog(context!!, R.style.ThemeBottomSheet)

                // on below line we are inflating a layout file which we have created.
                val card = LayoutInflater.from(context).inflate(R.layout.dialog_messages, null)

                val imageIcon = card.findViewById<ImageView>(R.id.messageIcon)
                val typeTitle = card.findViewById<TextView>(R.id.typeTitle)
                val typeInfo =card.findViewById<TextView>(R.id.typeInfo)

                    typeTitle.text = context.getString(R.string.changelog)
                    imageIcon.setImageResource(R.drawable.ic_outline_receipt_long_24)
                    typeInfo.text = item!!.changelog
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

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val comitLine: LinearLayout = itemView.findViewById(R.id.line1)
        val chidoriVersion: TextView = itemView.findViewById(R.id.chidori_version)
        val buildDate: TextView = itemView.findViewById(R.id.date)
        val commit: TextView = itemView.findViewById(R.id.commit)
        val changelogBTN: Button = itemView.findViewById(R.id.btnChangelog)
        fun bind(get: KernelUpdateResponse?) {
            chidoriVersion.text = "${get?.chidori.toString()} (${get?.kernel})"
            buildDate.text = get?.date

        }

    }



}