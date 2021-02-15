package com.craftrom.manager.utils.module

import android.os.AsyncTask
import com.craftrom.manager.fragments.modules.ModuleFragment
import java.io.IOException
import java.io.InputStream
import java.lang.ref.WeakReference
import java.net.HttpURLConnection
import java.net.URL

@Suppress("DEPRECATION")
class ModuleFeedFetcher(val context: ModuleFragment) : AsyncTask<URL, Void, List<ModuleItem>>() {
    private val reference = WeakReference(context)
    private var stream: InputStream? = null
    override fun doInBackground(vararg params: URL?): List<ModuleItem>? {

        val connect = params[0]?.openConnection() as HttpURLConnection
        connect.readTimeout = 8000
        connect.connectTimeout = 8000
        connect.requestMethod = "GET"
        connect.connect()
        val responseCode: Int = connect.responseCode
        var moduleItems: List<ModuleItem>? = null
        if (responseCode == 200) {
            stream = connect.inputStream
            try {
                val parser = ModuleParser()
                moduleItems = parser.parse(stream!!)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return moduleItems
    }
    override fun onPostExecute(result: List<ModuleItem>?) {
        super.onPostExecute(result)
        if (result != null && !result.isEmpty()) {
            reference.get()?.updateRV(result)
        }
    }


}