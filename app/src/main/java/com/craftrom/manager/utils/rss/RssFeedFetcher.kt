package com.craftrom.manager.utils.rss

import android.os.AsyncTask
import android.util.Log
import com.craftrom.manager.fragments.news.NewsFragment
import com.craftrom.manager.utils.Constants.Companion.TAG
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

class RssFeedFetcher(val context: NewsFragment) : AsyncTask<URL, Void, List<RssItem>>() {
    private val reference = WeakReference(context)
    private var stream: InputStream? = null
    override fun doInBackground(vararg params: URL?): List<RssItem>? {
        Log.d(TAG, "doInBackground Called With Parameter: $params")
        val connect = params[0]?.openConnection() as HttpURLConnection
        val responseCode: Int = connect.responseCode
        Log.d(TAG,"$TAG: Server Response Code = $responseCode")
        var rssItems: List<RssItem>? = null
        if (responseCode == 200) {
            stream = connect.inputStream
            try {
                val parser = RssParser()
                rssItems = parser.parse(stream!!)
            } catch (e: IOException) {
                Log.e(TAG, "RSS FETCHER: " + e.printStackTrace())
            }
        }else
        {
            Log.e(TAG, "NewsFragment: ERRORS")
            return null
        }
        return rssItems
    }
    override fun onPostExecute(result: List<RssItem>?) {
        super.onPostExecute(result)
        if (result != null) {
            reference.get()?.updateRV(result)
        } else
        {
            Log.e(TAG, "NewsFragment: ERRORS")
        }
    }


}