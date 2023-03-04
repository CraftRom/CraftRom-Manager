package com.craftrom.manager.ui.log

import android.annotation.SuppressLint
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.utils.Const.TAG

class LogAdapter : RecyclerView.Adapter<LogAdapter.LogItemViewHolder>() {

    private val logs: MutableList<LogItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_log, parent, false)
        return LogItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogItemViewHolder, position: Int) {
        val item = logs[position]
        holder.logLevel.text = item.level.toString().first().toString()
        when(item.level) {
            LogLevel.VERBOSE -> holder.logLevel.setBackgroundColor(Color.parseColor("#A9A9A9"))
            LogLevel.DEBUG -> holder.logLevel.setBackgroundColor(Color.parseColor("#007FFF"))
            LogLevel.INFO -> holder.logLevel.setBackgroundColor(Color.parseColor("#00CC00"))
            LogLevel.WARNING -> holder.logLevel.setBackgroundColor(Color.parseColor("#FFD700"))
            LogLevel.ERROR -> holder.logLevel.setBackgroundColor(Color.parseColor("#FF4500"))
            LogLevel.FATAL -> holder.logLevel.setBackgroundColor(Color.parseColor("#2F4F4F"))
        }
        holder.logTag.text = item.tag
        holder.logMessage.text = item.message
        holder.logDate.text = item.date
        holder.logPID.text = item.pid
        holder.logTID.text = item.tid
    }

    override fun getItemCount(): Int {
        return logs.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addLog(tag: String, message: String, level: LogLevel, date: String, pid: String, tid: String) {
        logs.add(LogItem(tag, message, level, date, pid, tid))
        notifyDataSetChanged()
    }

    fun sortByLogLevel(logLevel: LogLevel) {
        val filteredLogs = logs.filter { it.level == logLevel }
        val filteredLogsLevels = filteredLogs.map { it.level } // получаем все уровни отфильтрованных логов
        logs.clear()
        logs.addAll(filteredLogs)
        // Проверяем, что логи не пустые и что они все имеют один и тот же уровень логирования
        if (filteredLogs.isNotEmpty() && filteredLogsLevels.all { it == logLevel }) {
            notifyDataSetChanged()
        } else {
            // Если логи пустые или имеют разные уровни, уведомляем об этом через Logcat
            Log.e(TAG, "Filtered logs are empty or have different log levels")
        }
    }



    class LogItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logLevel: TextView = itemView.findViewById(R.id.log_level)
        val logTag: TextView = itemView.findViewById(R.id.log_tag)
        val logMessage: TextView = itemView.findViewById(R.id.log_message)
        val logDate: TextView = itemView.findViewById(R.id.log_date)
        val logPID: TextView = itemView.findViewById(R.id.log_pid)
        val logTID: TextView = itemView.findViewById(R.id.log_tid)
    }

    data class LogItem(val tag: String, val message: String, val level: LogLevel, val date: String, val pid: String, val tid: String)

    enum class LogLevel {
        VERBOSE, DEBUG, INFO, WARNING, ERROR, FATAL
    }
}
