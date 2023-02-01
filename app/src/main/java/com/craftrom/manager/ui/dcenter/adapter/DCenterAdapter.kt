package com.craftrom.manager.ui.dcenter.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.utils.DeviceSystemInfo
import com.craftrom.manager.utils.response.ContentUpdateResponse
import kotlin.math.min

class DCenterAdapter (
    private val context: FragmentActivity?,
    private val data: List<ContentUpdateResponse>?) : RecyclerView.Adapter<DCenterAdapter.MyHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_content_dcenter, parent, false)
        return MyHolder(v)
    }

    override fun getItemCount(): Int = data?.size?.let { min(it, 5) } ?: 0

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = data?.get(position)
        val currentVersion = DeviceSystemInfo.chidoriVersion()

        when (val version = item?.version) {
            "raccoon" -> holder.content_version.text = "$version (Android 11)"
            "squirrel" -> holder.content_version.text = "$version (Android 12)"
            "tortoise" -> holder.content_version.text = "$version (Android 13)"
            else -> {
                holder.content_version.text = version
            }
        }

        when (val type = item?.type) {
            "kernel" -> {
                holder.content_icon.setImageResource(R.drawable.ic_linux_kernel)
                holder.content_type.text = type
            }
            "kali" -> {
                holder.content_icon.setImageResource(R.drawable.ic_kali_kernel)
                holder.content_type.text = "kernel"
            }
            else -> {
                holder.content_icon.setImageResource(R.drawable.ic_system_android)
                holder.content_type.text = type
            }
        }

        holder.content_name.text = item?.name
        holder.content_device.text = DeviceSystemInfo.deviceCode() + "(" + DeviceSystemInfo.device() + ")"
        holder.content_date.text = item?.dateTime
    }

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content_icon: ImageView = itemView.findViewById(R.id.dcenter_icon)
        val content_name: TextView = itemView.findViewById(R.id.dcenter_title)
        val content_type: TextView = itemView.findViewById(R.id.dcenter_type_info)
        val content_device: TextView = itemView.findViewById(R.id.dcenter_device_codename_info)
        val content_version: TextView = itemView.findViewById(R.id.dcenter_version_info)
        val content_date: TextView = itemView.findViewById(R.id.dcenter_date_info)
    }
}