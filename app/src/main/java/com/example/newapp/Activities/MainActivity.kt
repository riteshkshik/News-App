package com.example.newapp.Activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
//import android.widget.SearchView

import androidx.appcompat.widget.SearchView

import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newapp.Adapters.NewsAdapter
import com.example.newapp.R
import com.example.newapp.api.news_api_call_for_search_query
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

        val toolbar = binding?.toolbar
        setSupportActionBar(toolbar)

        CoroutineScope(Dispatchers.IO).launch{
            // todo implement top headlines api call
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                Toast.makeText(this@MainActivity, query, Toast.LENGTH_SHORT).show()
                if (query != null && query.isNotEmpty()){
                    CoroutineScope(Dispatchers.IO).launch{
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
        val retrofitBuilder = Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api_interface_object = retrofitBuilder.create(news_api_call_for_search_query::class.java);
        val call = api_interface_object.send_request(query, "b869fe433b5841acbfd5c14a804d5837")
        call.enqueue(object : Callback<News>{
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val json_from_response = Gson().toJson(response.body())
                //Log.d("lksjfsdlfjsdksdlf", json_from_response.toString())
                val news = Gson().fromJson(json_from_response, News::class.java)
                showDataToRecyclerView(news.articles)
                Log.d("total_results", news.articles[0].description.toString())
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
        adapter.setOnItemClickListner(object : NewsAdapter.onItemClickListner {
            override fun onItemClick(position: Int) {
                val intent = Intent(this@MainActivity, DetailsActivity::class.java)
                intent.putExtra("news_url", newsList[position].url)
                startActivity(intent)

                //Toast.makeText(this@MainActivity, position.toString(), Toast.LENGTH_SHORT).show()
            }

        })
    }
}