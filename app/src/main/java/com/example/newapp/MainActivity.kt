package com.example.newapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newapp.api.news_api_call
import com.example.newapp.databinding.ActivityMainBinding
import com.example.newapp.models.Article
import com.example.newapp.models.News
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        CoroutineScope(Dispatchers.IO).launch{
            test()
        }
    }
    fun test(){
        val retrofitBuilder = Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api_interface_object = retrofitBuilder.create(news_api_call::class.java);
        val call = api_interface_object.send_request("in", "b869fe433b5841acbfd5c14a804d5837")
        call.enqueue(object : Callback<News>{
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val json_from_response = Gson().toJson(response.body())
                //Log.d("lksjfsdlfjsdksdlf", json_from_response.toString())
                val news = Gson().fromJson(json_from_response, News::class.java)
                showDataToRecyclerView(news.articles)
                Log.d("total_results", news.totalResults.toString())
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                Log.d("lksjfsdlfjsdksdlf", "failed")
            }

        })
    }

    fun showDataToRecyclerView(newsList: List<Article>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.reclerViewMain?.layoutManager = layoutManager

        val adapter = NewsAdapter(this, newsList)
        binding?.reclerViewMain?.adapter = adapter
    }
}