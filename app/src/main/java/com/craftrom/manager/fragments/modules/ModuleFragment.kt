package com.craftrom.manager.fragments.modules

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import com.craftrom.manager.R
import com.craftrom.manager.adapter.ModuleItemRecyclerViewAdapter
import com.craftrom.manager.utils.module.ModuleItem
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ModuleFragment : Fragment() {

    private var URL = "https://raw.githubusercontent.com/CraftRom/KernelUpdates/android-10/module.xml"
    lateinit var listView_details: ListView
    var arrayList_details:ArrayList<ModuleItem> = ArrayList();
    //OkHttpClient creates connection pool between client and server
    val client = OkHttpClient()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_module, container, false)


        listView_details = root.findViewById(R.id.listView) as ListView
        run(URL)
        return root
    }

    private fun run(url: String) {
        val request = Request.Builder()
                .url(url)
                .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
            }

            override fun onResponse(call: Call, response: Response) {
                val str_response = response.body()!!.string()
                //creating json object
                val json_contact:JSONObject = JSONObject(str_response)
                //creating json array
                val jsonarray_info: JSONArray = json_contact.getJSONArray("info")
                val size:Int = jsonarray_info.length()
                arrayList_details= ArrayList();
                for (i in 0 until size) {
                    val json_objectdetail:JSONObject=jsonarray_info.getJSONObject(i)
                    val model:ModuleItem= ModuleItem();
                    model.title=json_objectdetail.getString("title")
                    model.description=json_objectdetail.getString("description")
                    model.pubDate=json_objectdetail.getString("date")
                    model.author=json_objectdetail.getString("author")
                    model.link=json_objectdetail.getString("link")
                    arrayList_details.add(model)
                }

                activity?.runOnUiThread {
                    //stuff that updates ui
                    val obj_adapter = ModuleItemRecyclerViewAdapter(context!!,arrayList_details)
                    listView_details.adapter=obj_adapter
                }

            }
        })
    }
}