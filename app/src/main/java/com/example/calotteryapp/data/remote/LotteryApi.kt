package com.example.calotteryapp.data.remote

import retrofit2.Response
import retrofit2.http.GET

interface LotteryApi {
    @GET(".")
    suspend fun getWebsiteHTML(): Response<String>
}
