package com.craftrom.manager.fragments.tools

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import com.craftrom.manager.R
import com.craftrom.manager.services.FPS
import com.craftrom.manager.services.FPSTile
import com.craftrom.manager.utils.Constants.Companion.BATTERY_THERMAL_COOL_FILE
import com.craftrom.manager.utils.Constants.Companion.BATTERY_THERMAL_WARM_FILE
import com.craftrom.manager.utils.Utils
import com.topjohnwu.superuser.ShellUtils
import com.topjohnwu.superuser.io.SuFile


class ToolsFragment : Fragment() {
    private lateinit var switchFpsMeter : SwitchCompat
    private lateinit var switchBatteryThermal : SwitchCompat

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val root = inflater.inflate(R.layout.fragment_tools, container, false)
        switchFpsMeter = root.findViewById(R.id.switch_fps_meter)
        switchBatteryThermal = root.findViewById(R.id.switch_battery_thermal)
        // FPS On/Off Switch
        switchFpsMeter.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!Settings.canDrawOverlays(activity)) {
                    val packageName = context?.packageName
                    val intent = Intent(
                        "android.settings.action.MANAGE_OVERLAY_PERMISSION",
                        Uri.parse("package:$packageName")
                    )
                    startActivityForResult(intent, 1)
                } else {
                    context?.startForegroundService(Intent(activity, FPS::class.java))
                }
            } else {
                context?.stopService(Intent(activity, FPS::class.java))
            }

            // Get FPS Running
            getFPSMeter()
        }

        // Battery Thermal Switch Listener
        switchBatteryThermal.setOnCheckedChangeListener { _, isChecked ->
            if (Utils.rootCheck(activity)) {
                if (isChecked) {
                    ShellUtils.fastCmd("echo 450 > $BATTERY_THERMAL_COOL_FILE")
                    ShellUtils.fastCmd("echo 490 > $BATTERY_THERMAL_WARM_FILE")
                    return@setOnCheckedChangeListener
                }
                ShellUtils.fastCmd("echo 420 > $BATTERY_THERMAL_COOL_FILE")
                ShellUtils.fastCmd("echo 450 > $BATTERY_THERMAL_WARM_FILE")
            }
            getBatteryThermal()
        }

        return root
    }


    // Get FPS Running
    private fun getFPSMeter() {
        switchFpsMeter.isChecked = isFPSServiceAlive()
        FPSTile.Service.isRunning = isFPSServiceAlive()
    }

    // Check FPS Service State
    private fun isFPSServiceAlive(): Boolean {
        val manager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager?
        for (service in manager!!.getRunningServices(Int.MAX_VALUE)) {
            if (FPS::class.java.name == service.service.className) {
                return true
            }
        }
        return false
    }

    // Battery Thermal Switch State
    private fun getBatteryThermal() {
        if (!SuFile.open(BATTERY_THERMAL_COOL_FILE).exists() || !SuFile.open(
                BATTERY_THERMAL_WARM_FILE
            ).exists()
        ) {
            return
        }
        if (ShellUtils.fastCmd("cat $BATTERY_THERMAL_COOL_FILE")
                .toInt() > 420 || ShellUtils.fastCmd("cat $BATTERY_THERMAL_WARM_FILE").toInt() > 450
        ) {
            switchBatteryThermal.isChecked = true
        }
    }

    // Refresh Fragment
    override fun onStart() {
        super.onStart()
        getFPSMeter()
        getBatteryThermal()
    }

}