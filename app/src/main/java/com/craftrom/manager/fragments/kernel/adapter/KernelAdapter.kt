package com.craftrom.manager.fragments.kernel.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.MainApplication
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants.Companion.KERNEL_NAME
import com.craftrom.manager.utils.DeviceSystemInfo
import com.craftrom.manager.utils.downloader.DownloadManagerUtil
import com.craftrom.manager.utils.updater.response.KernelUpdateResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.min

class KernelAdapter (
    private val context: FragmentActivity?,
    val data: List<KernelUpdateResponse>?) : RecyclerView.Adapter<KernelAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_new_kernel, parent, false)
        return MyHolder(v)
    }

    override fun getItemCount(): Int = data?.size?.let { min(it, 5) } ?: 0

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        holder.bind(data?.get(position))
        val item = data?.get(position)
        val currentVersion = DeviceSystemInfo.chidoriVersion()
        val newVersion = item?.chidori.toString()
        val title = context?.getString(R.string.app_name)
        val desc = "$currentVersion -> $newVersion"
        val kernel_title = context?.getString(R.string.kernel_version_author,
            item?.date, item?.chidori, item?.kernel)
            holder.version_author.text = kernel_title


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

        holder.downloadBTN.setOnClickListener {
            downloadOnly(context, item?.downloadLink.toString(), title!!, item!!.fileName, desc)
        }
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val kernel_title: TextView = itemView.findViewById(R.id.kernel_title)
        val description: TextView = itemView.findViewById(R.id.kernel_description)
        val version_author: TextView = itemView.findViewById(R.id.kernel_version_author)
        val changelogBTN: Button = itemView.findViewById(R.id.kernel_changelog)
        val downloadBTN: Button = itemView.findViewById(R.id.kernel_download)
        fun bind(get: KernelUpdateResponse?) {
            kernel_title.text = "$KERNEL_NAME Kernel"
            description.text = get?.commit

        }

    }
    private fun downloadOnly(activity: FragmentActivity?, url: String, title: String, fileName: String, desc: String){
        val dm = DownloadManagerUtil(activity!!)
        if (dm.checkDownloadManagerEnable()) {
            if (MainApplication.downloadId != 0L) {
                dm.clearCurrentTask(MainApplication.downloadId) // 先清空之前的下载
            }
            MainApplication.downloadId = dm.download(url, title, fileName, desc, false)
        }else{
            Toast.makeText(activity,"False download", Toast.LENGTH_SHORT).show()
        }
    }



}