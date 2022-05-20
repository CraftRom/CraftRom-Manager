package com.craftrom.manager.utils.updater.retrofit

import com.craftrom.manager.utils.updater.response.KernelUpdateResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface RetrofitClientInterface {

    @Headers(
        "Content-type:application/json"
    )

    @GET("{device}/{type}.json")
    fun kernel(@Path("device") device: String,@Path("type") type: String): Call<List<KernelUpdateResponse>>
}