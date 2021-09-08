package com.craftrom.manager.fragments.jitter

import android.os.*
import android.view.*
import android.view.Window.OnFrameMetricsAvailableListener
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.craftrom.manager.R
import com.craftrom.manager.utils.jitter.AnimatedBackgroundDrawable
import com.craftrom.manager.utils.jitter.PointGraphView
import kotlin.math.abs

class JitterFragment : Fragment() {

    private var jitterReport: TextView? = null
    private var uiFrameTimeReport: TextView? = null
    private var renderThreadTimeReport: TextView? = null
    private var totalFrameTimeReport: TextView? = null
    private var mostlyTotalFrameTimeReport: TextView? = null
    private var graph: PointGraphView? = null

    companion object {
        private var frameMetricsHandler: Handler? = null

        init {
            val thread = HandlerThread("frameMetricsListener")
            thread.start()
            frameMetricsHandler = Handler(thread.looper)
        }
    }

    private val updateHandler: Handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                R.id.jitter_mma -> jitterReport?.text = msg.obj as CharSequence
                R.id.totalish_mma -> mostlyTotalFrameTimeReport?.text = msg.obj as? CharSequence
                R.id.ui_frametime_mma -> uiFrameTimeReport?.text = msg.obj as? CharSequence
                R.id.rt_frametime_mma -> renderThreadTimeReport?.text = msg.obj as? CharSequence
                R.id.total_mma -> totalFrameTimeReport?.text = msg.obj as? CharSequence
                R.id.graph -> graph?.addJitterSample(msg.arg1, msg.arg2)
            }
        }
    }

    private val frameMetricsListener: OnFrameMetricsAvailableListener =
        object : OnFrameMetricsAvailableListener {
            private val WEIGHT = 40.0
            private var previousFrameTotal: Long = 0
            private var jitterMma = 0.0
            private var uiFrameTimeMma = 0.0
            private var rtFrameTimeMma = 0.0
            private var totalFrameTimeMma = 0.0
            private var mostlyTotalFrameTimeMma = 0.0
            private var needsFirstValues = true
            override fun onFrameMetricsAvailable(
                window: Window, frameMetrics: FrameMetrics,
                dropCountSinceLastInvocation: Int,
            ) {
                if (frameMetrics.getMetric(FrameMetrics.FIRST_DRAW_FRAME) == 1L) {
                    return
                }
                val uiDuration = (frameMetrics.getMetric(FrameMetrics.INPUT_HANDLING_DURATION)
                        + frameMetrics.getMetric(FrameMetrics.ANIMATION_DURATION)
                        + frameMetrics.getMetric(FrameMetrics.LAYOUT_MEASURE_DURATION)
                        + frameMetrics.getMetric(FrameMetrics.DRAW_DURATION))
                val rtDuration = (frameMetrics.getMetric(FrameMetrics.SYNC_DURATION)
                        + frameMetrics.getMetric(FrameMetrics.COMMAND_ISSUE_DURATION))
                val totalDuration = frameMetrics.getMetric(FrameMetrics.TOTAL_DURATION)
                val jitter = abs(totalDuration - previousFrameTotal)
                if (needsFirstValues) {
                    jitterMma = 0.0
                    uiFrameTimeMma = uiDuration.toDouble()
                    rtFrameTimeMma = rtDuration.toDouble()
                    totalFrameTimeMma = totalDuration.toDouble()
                    mostlyTotalFrameTimeMma = (uiDuration + rtDuration).toDouble()
                    needsFirstValues = false
                } else {
                    jitterMma = add(jitterMma, jitter.toDouble())
                    uiFrameTimeMma = add(uiFrameTimeMma, uiDuration.toDouble())
                    rtFrameTimeMma = add(rtFrameTimeMma, rtDuration.toDouble())
                    totalFrameTimeMma = add(totalFrameTimeMma, totalDuration.toDouble())
                    mostlyTotalFrameTimeMma =
                        add(mostlyTotalFrameTimeMma, (uiDuration + rtDuration).toDouble())
                }
                previousFrameTotal = totalDuration

                updateHandler.obtainMessage(
                    R.id.jitter_mma,
                    String.format("Jitter: %.3fms", toMs(jitterMma))
                ).sendToTarget()
                updateHandler.obtainMessage(
                    R.id.totalish_mma,
                    context?.getString(R.string.totalish_mma, toMs(mostlyTotalFrameTimeMma))
                ).sendToTarget()
                updateHandler.obtainMessage(
                    R.id.ui_frametime_mma,
                    context?.getString(R.string.ui_frametime_mma, toMs(uiFrameTimeMma))
                ).sendToTarget()
                updateHandler.obtainMessage(
                    R.id.rt_frametime_mma,
                    context?.getString(R.string.rt_frametime_mma, toMs(rtFrameTimeMma))
                ).sendToTarget()
                updateHandler.obtainMessage(
                    R.id.total_mma,
                    context?.getString(R.string.total_mma, toMs(totalFrameTimeMma))
                ).sendToTarget()
                updateHandler.obtainMessage(
                    R.id.graph, (jitter / 1000).toInt(),
                    (jitterMma / 1000).toInt()
                ).sendToTarget()
            }

            fun add(previous: Double, today: Double): Double {
                return ((WEIGHT - 1) * previous + today) / WEIGHT
            }

            fun toMs(value: Double): Double {
                return value / 1000000
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_jitter, container, false)
        val content = requireActivity().findViewById<View>(android.R.id.content)
        content.background = AnimatedBackgroundDrawable()
        content.keepScreenOn = true
        jitterReport = root.findViewById(R.id.jitter_mma)
        mostlyTotalFrameTimeReport = root.findViewById(R.id.totalish_mma)
        uiFrameTimeReport = root.findViewById(R.id.ui_frametime_mma)
        renderThreadTimeReport = root.findViewById(R.id.rt_frametime_mma)
        totalFrameTimeReport = root.findViewById(R.id.total_mma)
        graph = root.findViewById(R.id.graph)
        jitterReport?.text = "abcdefghijklmnopqrstuvwxyz"
        mostlyTotalFrameTimeReport?.text = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        uiFrameTimeReport?.text = "012345689"
        renderThreadTimeReport?.text = ",.!()[]{};"
        requireActivity().window.addOnFrameMetricsAvailableListener(frameMetricsListener, frameMetricsHandler)
        return root
    }
}