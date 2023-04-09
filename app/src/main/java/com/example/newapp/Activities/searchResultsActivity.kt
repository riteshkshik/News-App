package com.example.newapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newapp.Adapters.NewsAdapter
import com.example.newapp.R
import com.example.newapp.api.news_api_call_for_search_query
import com.example.newapp.databinding.ActivitySearchResultsBinding
import com.example.newapp.models.Article
import com.example.newapp.models.News
import com.example.newapp.utils.Constants
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class searchResultsActivity : AppCompatActivity() {
    var binding: ActivitySearchResultsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchResultsBinding.inflate(layoutInflater)
        setContentView(binding?.root)
        setSupportActionBar()
        if (intent.hasExtra(Constants.SEARCHED_STRING)) {
            val search_query = intent.getStringExtra(Constants.SEARCHED_STRING)
            CoroutineScope(Dispatchers.IO).launch {
                if (search_query.isNullOrEmpty()) {
                    Toast.makeText(this@searchResultsActivity, "Something Went Wrong!!", Toast.LENGTH_SHORT).show()
                } else {
                    searchNewsFromQuery(search_query)
                }
            }
        }
    }

    fun setSupportActionBar() {
        val toolbar = binding?.toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = ""
        }
        binding?.backpressButtonToolbar?.setOnClickListener { onBackPressed() }
    }

    fun searchNewsFromQuery(query: String) {
        binding?.pgBar?.visibility = View.VISIBLE
        val retrofitBuilder = Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api_interface_object =
            retrofitBuilder.create(news_api_call_for_search_query::class.java);
        val call = api_interface_object.send_request(query, Constants.API_KEY)
        call.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val json_from_response = Gson().toJson(response.body())
                val news = Gson().fromJson(json_from_response, News::class.java)
                showDataToRecyclerView(news.articles)
                binding?.pgBar?.visibility = View.GONE
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                binding?.pgBar?.visibility = View.GONE
            }

        })
    }

    fun showDataToRecyclerView(newsList: List<Article>) {
        val layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        binding?.searchResultsRecyclerview?.layoutManager = layoutManager

        val adapter = NewsAdapter(this, newsList)
        binding?.searchResultsRecyclerview?.adapter = adapter
        adapter.setOnItemClickListner(object : NewsAdapter.onItemClickListner {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@searchResultsActivity, DetailsActivity::class.java)
                intent.putExtra("news_url", newsList[position].url)
                startActivity(intent)
            }
        })
    }
}