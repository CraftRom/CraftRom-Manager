package com.craftrom.manager.services.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import org.w3c.dom.Text


class CpuItemRecyclerViewAdapter(private val data: List<String>) :
    RecyclerView.Adapter<CpuItemRecyclerViewAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val rowItem: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_cpu_freq, parent, false)
        return ViewHolder(rowItem)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = data[position] + " MHz"
        holder.cpuText.text = "CPU$position"
    }

    override fun getItemCount(): Int {
        return this.data.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.tv_cpu)
        val cpuText: TextView = view. findViewById(R.id.head_cpu)

    }
}