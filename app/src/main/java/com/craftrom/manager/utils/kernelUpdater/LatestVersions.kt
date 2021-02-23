package com.craftrom.manager.utils.kernelUpdater

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.androidnetworking.interfaces.StringRequestListener
import com.craftrom.manager.R
import com.craftrom.manager.utils.Constants
import kotlinx.android.synthetic.main.latest_version.*
import org.json.JSONObject

class LatestVersions: AppCompatActivity() {

    lateinit var host: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AndroidNetworking.initialize(this@LatestVersions)
        setContentView(R.layout.latest_version)



        AndroidNetworking
            .get(Constants.HOST_REFERENCE)
            .doNotCacheResponse()
            .build()
            .getAsString(object: StringRequestListener {
                override fun onResponse(response: String) {
                    host = response
                    getLinks()
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(this@LatestVersions, "Please check your internet connection.", Toast.LENGTH_LONG).show()
                    finish()
                }
            })
    }

    private fun getLinks(){
        AndroidNetworking
            .get("$host/kernel.json")
            .doNotCacheResponse()
            .build()
            .getAsJSONObject(object: JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    lPCodename.text = response.getString("latestQCodename")
                    lQCodename.text = response.getString("latestRCodename")
                    pTagLatest.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(response.getString("latestQLink"))))
                    }
                    qTagLatest.setOnClickListener {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(response.getString("latestRLink"))))
                    }
                    pTagLatest.visibility = View.VISIBLE
                    qTagLatest.visibility = View.VISIBLE
                }

                override fun onError(anError: ANError?) {
                    Toast.makeText(this@LatestVersions, "Please check your internet connection!", Toast.LENGTH_LONG).show()
                    finish()
                }
            })
    }

    override fun onBackPressed() {
        finish()
    }
}