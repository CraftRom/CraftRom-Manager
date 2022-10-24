package com.craftrom.manager.utils.service

import com.craftrom.manager.utils.rss.RssConverterFactory
import com.craftrom.manager.utils.rss.RssFeed

import retrofit2.Call
import retrofit2.Retrofit

object RetrofitInstance {

    fun setupRetrofitCall(feedsBaseUrl : String, feedsUrlEndPoint : String) : Call<RssFeed> {

        val retrofit = Retrofit.Builder()
            .baseUrl(feedsBaseUrl)
            .addConverterFactory(RssConverterFactory.create())
            .build()

        val service = retrofit.create(RssService::class.java)

        return service.getRss(feedsUrlEndPoint)
    }
}