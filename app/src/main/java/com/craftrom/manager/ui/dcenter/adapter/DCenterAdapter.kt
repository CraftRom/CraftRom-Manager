package com.craftrom.manager.ui.dcenter.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.core.ServiceContext.context
import com.craftrom.manager.ui.view.ItemWebViewActivity
import com.craftrom.manager.utils.Const
import com.craftrom.manager.utils.DeviceSystemInfo
import com.craftrom.manager.utils.response.ContentUpdateResponse
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlin.math.min

class DCenterAdapter : RecyclerView.Adapter<DCenterAdapter.MyHolder>() {
    private lateinit var type: String
    private var contentList: List<ContentUpdateResponse> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val viewInflated = layoutInflater.inflate(R.layout.item_content_dcenter, parent, false)
        return MyHolder(viewInflated)
    }

    fun setData(contentList: List<ContentUpdateResponse>) {
        this.contentList = contentList
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: MyHolder, position: Int) {
        val item = contentList[position]
        val types = item.type

        when (val version = item.version) {
            "raccoon" -> holder.content_version.text = "$version (Android 11)"
            "squirrel" -> holder.content_version.text = "$version (Android 12)"
            "tortoise" -> holder.content_version.text = "$version (Android 13)"
            else -> {
                holder.content_version.text = version ?: ""
            }
        }

        type = when (types) {
            "kernel" -> {
                val link_chid = "${Const.CHIDORI_FILE_URL}${DeviceSystemInfo.deviceCode()}/stable/${item.fileName}/download"
                holder.button_download.setOnClickListener {
                    val intent = Intent(context, ItemWebViewActivity::class.java).apply {
                        putExtra("feedItemUrl", link_chid)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
                if (DeviceSystemInfo.chidoriName() == Const.KERNEL_NAME) {
                    holder.content_statusIcon.visibility = View.VISIBLE
                    holder.content_statusIcon.setImageResource(R.drawable.ic_check_decagram)
                } else {
                    holder.content_statusIcon.visibility = View.GONE
                }
                holder.content_icon.setImageResource(R.drawable.ic_linux_kernel)
                types
            }
            "kali" -> {
                if (DeviceSystemInfo.tsukuyoumiName() == Const.KALI_NAME) {
                    holder.content_statusIcon.visibility = View.VISIBLE
                    holder.content_statusIcon.setImageResource(R.drawable.ic_check_decagram)
                } else {
                    holder.content_statusIcon.visibility = View.GONE
                }
                holder.content_icon.setImageResource(R.drawable.ic_kali_kernel)
                "kernel"
            }
            else -> {
                val link_rom = "${Const.EXODUS_FILE_URL}${DeviceSystemInfo.deviceCode()}/${item.version}/${item.fileName}/download"
                holder.button_download.setOnClickListener {
                    val intent = Intent(context, ItemWebViewActivity::class.java).apply {
                        putExtra("feedItemUrl", link_rom)
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(intent)
                }
                if (DeviceSystemInfo.exodusVersion().isNotEmpty())
                {
                    holder.content_statusIcon.visibility =View.VISIBLE
                    holder.content_statusIcon.setImageResource(R.drawable.ic_check_decagram)
                } else {
                    holder.content_statusIcon.visibility =View.GONE
                }
                holder.content_icon.setImageResource(R.drawable.ic_system_android)
                types.toString()
            }
        }
        holder.content_type.text = type
        holder.content_name.text = item.name
        holder.content_device.text = DeviceSystemInfo.deviceCode() + "(" + DeviceSystemInfo.device() + ")"
        holder.content_date.text = item.dateTime
        holder.content_desc.text = item.desc

        holder.button_changelog.setOnClickListener {
            // створюємо новий bottom sheet діалог
            val dialog = BottomSheetDialog(context, R.style.ThemeBottomSheet)

            // надуваємо наш layout файл
            val card = LayoutInflater.from(context).inflate(R.layout.dialog_content, null, false)

            // знаходимо всі необхідні views в нашому layout
            val dIcon = card.findViewById<ImageView>(R.id.dialogIcon)
            val dTitle = card.findViewById<TextView>(R.id.dialogTitle)
            val dContent = card.findViewById<TextView>(R.id.dialogContent)

            // встановлюємо значення для views
            dIcon.setImageResource(R.drawable.ic_list)
            dTitle.text = "Changelog"
            dContent.text = type

            // встановлюємо можливість закриття діалогу при кліку на зовнішню область
            dialog.setCancelable(true)

            // встановлюємо наш layout як контент діалогу
            dialog.setContentView(card)

            // встановлюємо анімацію закриття діалогу
            dialog.dismissWithAnimation = true

            // відображаємо діалог
            dialog.show()
        }
    }

    override fun getItemCount(): Int = min(contentList.size, 5)

    class MyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val content_icon: ImageView = itemView.findViewById(R.id.dcenter_icon)
        val content_statusIcon: ImageView = itemView.findViewById(R.id.dcenter_status_icon)
        val content_name: TextView = itemView.findViewById(R.id.dcenter_title)
        val content_type: TextView = itemView.findViewById(R.id.dcenter_type_info)
        val content_device: TextView = itemView.findViewById(R.id.dcenter_device_codename_info)
        val content_version: TextView = itemView.findViewById(R.id.dcenter_version_info)
        val content_date: TextView = itemView.findViewById(R.id.dcenter_date_info)
        val content_desc: TextView = itemView.findViewById(R.id.dcenter_desc_content)

        val button_download: Button = itemView.findViewById(R.id.dcenter_button_download)
        val button_changelog: Button = itemView.findViewById(R.id.dcenter_button_changelog)
    }
}