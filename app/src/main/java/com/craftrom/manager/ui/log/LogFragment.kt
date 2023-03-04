package com.craftrom.manager.ui.log

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.MainActivity
import com.craftrom.manager.R
import com.craftrom.manager.core.ServiceContext
import com.craftrom.manager.ui.log.LogAdapter.LogLevel.*
import com.craftrom.manager.utils.Const
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

class LogFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var logAdapter: LogAdapter
    private lateinit var scrollDownButton: FloatingActionButton
    private lateinit var scrollUpButton: FloatingActionButton

    private var lastLogDate: String? = null
    private val ioScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_log, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)
        logAdapter = LogAdapter()
        updateTitle()
        var userScrolledList = false // Флаг, указывающий на то, что пользователь прокрутил список вручную

        // получаем ссылки на RecyclerView и создаем адаптер
        recyclerView = view.findViewById(R.id.recyclerView)
        scrollDownButton = view.findViewById(R.id.downButton)
        scrollUpButton = view.findViewById(R.id.upButton)

        // устанавливаем адаптер в RecyclerView
        recyclerView.adapter = logAdapter
        recyclerView.layoutManager = LinearLayoutManager(activity)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            private var lastDy = 0
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    userScrolledList = true
                }
                // If the RecyclerView has stopped scrolling, and it was scrolled up, show the scroll button
                if (newState == RecyclerView.SCROLL_STATE_IDLE){
                    if(lastDy < 0 ){
                        scrollDownButton.apply {
                            alpha = 1f
                            visibility = View.GONE
                            animate()
                                .alpha(0f)
                                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                                .start()
                        }
                        scrollUpButton.apply {
                            alpha = 0f
                            visibility = View.VISIBLE
                            animate()
                                .alpha(1f)
                                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                                .start()
                        }
                    }
                    if(lastDy > 0 ){
                        scrollDownButton.apply {
                            alpha = 0f
                            visibility = View.VISIBLE
                            animate()
                                .alpha(1f)
                                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                                .start()
                        }
                        scrollUpButton.apply {
                            alpha = 1f
                            visibility = View.GONE
                            animate()
                                .alpha(0f)
                                .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                                .start()
                        }
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (dy < 0 ) { // If the RecyclerView is scrolled up, show the scroll button
                    scrollDownButton.apply {
                        alpha = 1f
                        visibility = View.GONE
                        animate()
                            .alpha(0f)
                            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                            .start()
                    }
                    scrollUpButton.apply {
                        alpha = 0f
                        visibility = View.VISIBLE
                        animate()
                            .alpha(1f)
                            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                            .start()
                    }
                    userScrolledList = true
                } else if (dy > 0 ) { // If the RecyclerView is scrolled down, show the scroll button
                    scrollDownButton.apply {
                        alpha = 0f
                        visibility = View.VISIBLE
                        animate()
                            .alpha(1f)
                            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                            .start()
                    }
                    scrollUpButton.apply {
                        alpha = 1f
                        visibility = View.GONE
                        animate()
                            .alpha(0f)
                            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                            .start()
                    }
                    userScrolledList = true
                } else if (!recyclerView.canScrollVertically(1)) { // If the RecyclerView is scrolled down or at the bottom, hide the scroll button
                    scrollDownButton.apply {
                        alpha = 1f
                        visibility = View.GONE
                        animate()
                            .alpha(0f)
                            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                            .start()
                    }
                    scrollUpButton.apply {
                        alpha = 1f
                        visibility = View.GONE
                        animate()
                            .alpha(0f)
                            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                            .start()
                    }
                    userScrolledList = false
                } else {
                    scrollDownButton.apply {
                        alpha = 1f
                        visibility = View.GONE
                        animate()
                            .alpha(0f)
                            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                            .start()
                    }
                    scrollUpButton.apply {
                        alpha = 1f
                        visibility = View.GONE
                        animate()
                            .alpha(0f)
                            .setDuration(resources.getInteger(android.R.integer.config_shortAnimTime).toLong())
                            .start()
                    }
                    userScrolledList = true
                }

                lastDy = dy
            }
        })

        // Add a click listener to the scroll button to scroll to the bottom of the list
        scrollDownButton.setOnClickListener {
            recyclerView.scrollToPosition(logAdapter.itemCount - 1)
            userScrolledList = false
        }
        scrollUpButton.setOnClickListener {
            recyclerView.scrollToPosition(0)
        }

        ioScope.launch {
            var process: Process? = null
            var reader: BufferedReader? = null
            val numCPUCores = Runtime.getRuntime().availableProcessors().coerceAtLeast(1)
            val executor = Executors.newFixedThreadPool(numCPUCores)
            Log.i("LogFragment", "CPU core: $numCPUCores")
            try {
                val sharedPreferences =
                    PreferenceManager.getDefaultSharedPreferences(ServiceContext.context)
                val rootEnabled = sharedPreferences.getBoolean(Const.PREF_KEY_ROOT_ENABLE, false)
                val processBuilder = if (rootEnabled) {
                    ProcessBuilder("su", "-c", "logcat")
                } else {
                    ProcessBuilder("logcat", "-d")
                }
                process = withContext(Dispatchers.IO) {
                    processBuilder.start()
                }
                reader = BufferedReader(InputStreamReader(process.inputStream))
                while (true) {
                    if (isActive) { // check for cancellation
                        val line =
                            withContext(Dispatchers.IO) {
                                executor.submit<String> {
                                    reader.readLine()
                                }.get()
                            } ?: break
                        val logLevel = getLogLevel(line)
                        if (logLevel != null) {
                            val logData = getLogData(line)
                            if (logData != null && (logData.date != lastLogDate)) {
                                withContext(Dispatchers.Main) {
                                    logAdapter.addLog(
                                        logData.tag,
                                        logData.message,
                                        logLevel,
                                        logData.date,
                                        logData.pid,
                                        logData.tid
                                    )
                                    updateTitle()
                                    if (!userScrolledList) {
                                        recyclerView.scrollToPosition(logAdapter.itemCount - 1)
                                    }
                                }
                                lastLogDate = logData.date
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("LogFragment", "Error reading logs: ${e.message}")
            } finally {
                reader?.close()
                process?.destroy()
                executor.shutdown()
            }
        }

    }

    private fun updateTitle() {
        val titleName: String = getString(R.string.title_logs)
        val subtitle: String = getString(R.string.subtitle_logs)
        val itemCount = logAdapter.itemCount
        val title = "$titleName ($itemCount)"
        val mainActivity = requireActivity() as MainActivity
        mainActivity.setToolbarText(title, subtitle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        ioScope.cancel()
    }

    private fun getLogLevel(logLine: String): LogAdapter.LogLevel? {
        if (logLine.contains("V")) {
            return VERBOSE
        } else if (logLine.contains("D")) {
            return DEBUG
        } else if (logLine.contains("I")) {
            return INFO
        } else if (logLine.contains("W")) {
            return WARNING
        } else if (logLine.contains("E")) {
            return ERROR
        } else if (logLine.contains("F")) {
            return FATAL
        }
        return null
    }



    private fun getLogData(logLine: String): LogAdapter.LogItem? {
        val logDataRegex = Regex("(\\d{2}-\\d{2}\\s\\d{2}:\\d{2}:\\d{2}\\.\\d{3})\\s+(\\d+)\\s+(\\d+)\\s+([A-Z])\\s+(.+?):\\s+(.*)")
        val matchResult = logDataRegex.find(logLine)
        if (matchResult != null && matchResult.groups.size == 7) {
            val dateString = matchResult.groupValues[1]
            val tag = matchResult.groupValues[5]
            val message = matchResult.groupValues[6]
            val levelChar = matchResult.groupValues[4]
            val level = when (levelChar) {
                "D" -> DEBUG
                "I" -> INFO
                "W" -> WARNING
                "E" -> ERROR
                "F" -> FATAL
                else -> VERBOSE
            }
            val sdf = SimpleDateFormat("MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val timestamp = sdf.parse(dateString)?.time ?: 0
            val formattedDate = sdf.format(Date(timestamp))
            val pid = matchResult.groupValues[2]
            val tid = matchResult.groupValues[3]
            return LogAdapter.LogItem(tag, message, level, formattedDate, pid, tid)
        }
        Log.d("LogFragment", "Failed to parse log line: $logLine")
        Log.d("LogFragment", "Match result: ${matchResult?.value}")
        return null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_log_sort, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.sort_by -> {
                true
            }
            R.id.sort_debug -> {
                logAdapter.sortByLogLevel(DEBUG)
                item.isChecked = true
                true
            }
            R.id.sort_info -> {
                logAdapter.sortByLogLevel(INFO)
                item.isChecked = true
                true
            }
            R.id.sort_warning -> {
                logAdapter.sortByLogLevel(WARNING)
                item.isChecked = true
                true
            }
            R.id.sort_error -> {
                logAdapter.sortByLogLevel(ERROR)
                item.isChecked = true
                true
            }
            R.id.sort_fatal -> {
                logAdapter.sortByLogLevel(FATAL)
                item.isChecked = true
                true
            }
            R.id.sort_verbose -> {
                logAdapter.sortByLogLevel(VERBOSE)
                item.isChecked = true
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        ioScope.cancel()
    }
}
