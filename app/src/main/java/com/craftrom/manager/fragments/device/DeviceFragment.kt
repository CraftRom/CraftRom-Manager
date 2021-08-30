package com.craftrom.manager.fragments.device

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.craftrom.manager.R
import com.craftrom.manager.adapter.RssItemRecyclerViewAdapter
import com.craftrom.manager.utils.Device
import com.craftrom.manager.utils.RSS.RssItem
import com.craftrom.manager.utils.rom_checker.RomIdentifier.getRom
import com.craftrom.manager.utils.root.RootUtils
import com.craftrom.manager.utils.safetynet.presenter.DeviceFragmentPresenter
import com.craftrom.manager.utils.storage.isDiskEncrypted
import java.security.SecureRandom
import java.util.*
import kotlin.collections.ArrayList


class DeviceFragment : Fragment(){
    private val TAG = "CraftRom:Device"
    private val presenter = DeviceFragmentPresenter()
    private lateinit var  oem_name: TextView
    private lateinit var disk_status: TextView
    private lateinit var magisk_status: TextView
    private lateinit var android_codename: TextView
    private lateinit var android_version: TextView
    private lateinit var rom_version: TextView
    private lateinit var verify_safety_net: Button
    private lateinit var cts: TextView
    private lateinit var basic_int: TextView
    private lateinit var progressBar: ProgressBar
    private val mRandom: Random = SecureRandom()


    // TODO: Customize parameters
    private var columnCount = 1
    var adapter: RssItemRecyclerViewAdapter? = null
    var rssItems = ArrayList<RssItem>()
    var listV: RecyclerView ?= null

    var root: View? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val root = inflater.inflate(R.layout.fragment_device, container, false)
        oem_name = root.findViewById(R.id.oem_name)
        disk_status = root.findViewById(R.id.disk_status)
        android_codename = root.findViewById(R.id.android_codename)
        android_version = root.findViewById(R.id.android_version)
        rom_version = root.findViewById(R.id.rom_version)
        verify_safety_net = root.findViewById(R.id.verify_safety_net)
        cts = root.findViewById(R.id.ctsProfileMatch)
        basic_int = root.findViewById(R.id.basicIntegrity)
        progressBar = root.findViewById(R.id.progressBar)
        listV = root.findViewById(R.id.listV)
        verify_safety_net.setOnClickListener {
            showLoading(true);
        }


        arguments?.let {
            columnCount = it.getInt(ARG_COLUMN_COUNT)
        }
        InitUI()
        verify_safety_net.setOnClickListener { v ->
            context?.let { presenter.checkIntegrity(it) }
            showLoading(true)
        }
        return root
    }

    @Suppress("DEPRECATION")
    @SuppressLint("SetTextI18n")
    private fun InitUI() {
        val rom = getRom()

        oem_name.text = Device.getVendor() + " " + Device.getModel()
        if (RootUtils.rootAccess()){
            if (isDiskEncrypted) {
                disk_status.text = getString(R.string.disk_encrypted)
                disk_status.setTextColor(resources.getColor(R.color.colorTrue))
            } else {
                disk_status.text = getString(R.string.disk_not_encrypted)
                disk_status.setTextColor(resources.getColor(R.color.colorFalse))
            }
        } else {
            disk_status.text = getString(R.string.disk_not_permission)
            disk_status.setTextColor(resources.getColor(R.color.colorPermission))
        }

        android_codename.text  = Device.getDeviceName()
        android_version.text = Device.getVersion()
        rom_version.text = rom.name + " " + rom.versionName
        // presenter.nonceTV = findViewById(R.id.nonce)
        // presenter.timestampMsTV = findViewById(R.id.timestampMs)
        // presenter.apkPackageNameTV = findViewById(R.id.apkPackageName)
        // presenter.apkCertificateDigestSha256TV = findViewById(R.id.apkCertificateDigestSha256)
        // presenter.apkDigestSha256TV = findViewById(R.id.apkDigestSha256)
        presenter.basicIntegrityTV = root?.findViewById(R.id.basicIntegrity)
        presenter.ctsProfileMatchTV = root?.findViewById(R.id.ctsProfileMatch)
        // presenter.resultContainerLL = findViewById(R.id.resultContainer)
    }

    private fun showLoading(show: Boolean) {
        progressBar.setVisibility(if (show) View.VISIBLE else View.GONE)
        if (show) {
            verify_safety_net.setVisibility(View.GONE)
        }
    }




    companion object {

        // TODO: Customize parameter argument names
        const val ARG_COLUMN_COUNT = "column-count"

        // TODO: Customize parameter initialization
        @JvmStatic
        fun newInstance(columnCount: Int) =
            DeviceFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_COLUMN_COUNT, columnCount)
                }
            }
    }
}