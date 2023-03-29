package com.example.newapp.api

import com.example.newapp.models.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface news_api_call {
    @GET("top-headlines")
    fun send_request(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String
    ): Call<News>
}