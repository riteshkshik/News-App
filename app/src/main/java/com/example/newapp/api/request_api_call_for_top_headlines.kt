package com.example.newapp.api

import com.example.newapp.models.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface request_api_call_for_top_headlines {
    @GET("top-headlines")
    fun send_request(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("apiKey") apiKey: String
    ): Call<News>
}