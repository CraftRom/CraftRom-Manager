package com.craftrom.manager.utils.service

import com.craftrom.manager.utils.rss.RssFeed
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

interface RssService {
    @GET
    fun getRss(@Url url: String): Call<RssFeed>
}