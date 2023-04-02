package com.example.newapp.api

import com.example.newapp.models.News
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface news_api_call_for_top_headlines {
    @GET("everything")
    fun send_request(
        @Query("q") q: String,
        @Query("apiKey") apiKey: String
    ): Call<News>
}