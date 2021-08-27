package com.craftrom.manager.utils.rom

import android.os.AsyncTask
import com.craftrom.manager.fragments.rom.RomFragment
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class RomFeedFetcher(val context: RomFragment) : AsyncTask<URL, Void, List<RomItem>>() {
    private val reference = WeakReference(context)
    private var stream: InputStream? = null
    override fun doInBackground(vararg params: URL?): List<RomItem>? {

        val connect = params[0]?.openConnection() as HttpURLConnection
        connect.readTimeout = 8000
        connect.connectTimeout = 8000
        connect.requestMethod = "GET"
        connect.connect()
        val responseCode: Int = connect.responseCode
        var romItems: List<RomItem>? = null
        if (responseCode == 200) {
            stream = connect.inputStream
            try {
                val parser = RomParser()
                romItems = parser.parse(stream!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return romItems
    }
    override fun onPostExecute(result: List<RomItem>?) {
        super.onPostExecute(result)
        if (result != null && !result.isEmpty()) {
            reference.get()?.updateRV(result)
        }
    }


}