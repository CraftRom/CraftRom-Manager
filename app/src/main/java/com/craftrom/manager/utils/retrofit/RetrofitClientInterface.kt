package com.craftrom.manager.utils.retrofit

import com.craftrom.manager.utils.response.ContentUpdateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface RetrofitClientInterface {

    @Headers(
        "Content-type:application/json"
    )

    @GET("{device}.json")
    fun content(@Path("device") device: String): Call<List<ContentUpdateResponse>>
}