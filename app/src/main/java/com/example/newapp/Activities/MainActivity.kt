package com.example.newapp.Activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.ImageView
//import android.widget.SearchView

import androidx.appcompat.widget.SearchView

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newapp.Adapters.NewsAdapter
import com.example.newapp.R
import com.example.newapp.api.news_api_call_for_search_query
import com.example.newapp.api.news_api_call_for_top_headlines
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
    var savedNewsList: List<Article>? = null
    var isSearchViewUser = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar()

        CoroutineScope(Dispatchers.IO).launch{
            topHeadLinesNews()
        }
        binding?.backPressButton?.setOnClickListener {
            onBackPressed()
        }
    }

    fun setSupportActionBar(){
        val toolbar = binding?.toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
    }

    fun topHeadLinesNews(){
        binding?.pgBar?.visibility = View.VISIBLE
        val retrofitBuilder = Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api_interface_object = retrofitBuilder.create(news_api_call_for_top_headlines::class.java);
        val call = api_interface_object.send_request("in", "b869fe433b5841acbfd5c14a804d5837")
        call.enqueue(object : Callback<News>{
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val json_from_response = Gson().toJson(response.body())
                val news = Gson().fromJson(json_from_response, News::class.java)
                savedNewsList = news.articles
                showDataToRecyclerView(news.articles)
                binding?.pgBar?.visibility = View.GONE
            }

            override fun onFailure(call: Call<News>, t: Throwable) {
                binding?.pgBar?.visibility = View.GONE
            }

        })
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView
        searchView.queryHint = "Search Worldwide..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()){
                    CoroutineScope(Dispatchers.Main).launch{
                        item.collapseActionView()
                        isSearchViewUser = true
                        searchNewsFromQuery(query.toString())
                    }
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }
    fun searchNewsFromQuery(query: String){
        binding?.pgBar?.visibility = View.VISIBLE
        val retrofitBuilder = Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api_interface_object = retrofitBuilder.create(news_api_call_for_search_query::class.java);
        val call = api_interface_object.send_request(query, "b869fe433b5841acbfd5c14a804d5837")
        call.enqueue(object : Callback<News>{
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
        binding?.reclerViewMain?.layoutManager = layoutManager

        val adapter = NewsAdapter(this, newsList)
        binding?.reclerViewMain?.adapter = adapter
        adapter.setOnItemClickListner(object : NewsAdapter.onItemClickListner {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra("news_url", newsList[position].url)
                startActivity(intent)
            }
        })
    }

    override fun onBackPressed() {
        if (isSearchViewUser){
            showDataToRecyclerView(savedNewsList!!)
            isSearchViewUser = false
        }else{
            super.onBackPressed()
        }
    }
}