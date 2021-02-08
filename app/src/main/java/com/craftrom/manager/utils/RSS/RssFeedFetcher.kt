package com.craftrom.manager.utils.RSS

import android.os.AsyncTask
import com.craftrom.manager.fragments.device.DeviceFragment
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class RssFeedFetcher(val context: DeviceFragment) : AsyncTask<URL, Void, List<RssItem>>() {
    val reference = WeakReference(context)
    private var stream: InputStream? = null;
    override fun doInBackground(vararg params: URL?): List<RssItem>? {
        val connect = params[0]?.openConnection() as HttpURLConnection
        connect.readTimeout = 8000
        connect.connectTimeout = 8000
        connect.requestMethod = "GET"
        connect.connect();
        val responseCode: Int = connect.responseCode;
        var rssItems: List<RssItem>? = null
        if (responseCode == 200) {
            stream = connect.inputStream;
            try {
                val parser = RssParser()
                rssItems = parser.parse(stream!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return rssItems
    }
    override fun onPostExecute(result: List<RssItem>?) {
        super.onPostExecute(result)
        if (result != null && !result.isEmpty()) {
            reference.get()?.updateRV(result)
        }
    }
}