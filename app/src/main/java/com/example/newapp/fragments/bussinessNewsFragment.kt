package com.example.newapp.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.newapp.Activities.DetailsActivity
import com.example.newapp.Activities.MainActivity
import com.example.newapp.Adapters.NewsAdapter
import com.example.newapp.R
import com.example.newapp.api.request_api_call_for_top_headlines
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

class bussinessNewsFragment : Fragment() {

    lateinit var recyclerViewFragment: RecyclerView
    @SuppressLint("MissingInflatedId")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bussiness_news, container, false)
        recyclerViewFragment = view.findViewById(R.id.business_new_recyclerview)
        if (MainActivity.businessNewsData == null){
            CoroutineScope(Dispatchers.IO).launch {
                topHeadLinesNews(Constants.BUSINESS)
            }
        }else{
            showDataToRecyclerView(MainActivity.businessNewsData!!)
        }
        return view
    }


    fun topHeadLinesNews(category: String) {
        val retrofitBuilder = Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api_interface_object =
            retrofitBuilder.create(request_api_call_for_top_headlines::class.java);
        val call = api_interface_object.send_request(
            "in",
            category = category,
            Constants.API_KEY
        )
        call.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val json_from_response = Gson().toJson(response.body())
                val news = Gson().fromJson(json_from_response, News::class.java)
                if (news != null) {
                    MainActivity.businessNewsData = news.articles
                    showDataToRecyclerView(news.articles)
                }else{
                    Toast.makeText(requireContext(), "Per Day Api request limit exceeded!", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {

            }

        })

    }


    fun showDataToRecyclerView(newsList: List<Article>) {
        val layoutManager = LinearLayoutManager(requireContext())
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        recyclerViewFragment.layoutManager = layoutManager

        val adapter = NewsAdapter(requireContext(), newsList)
        recyclerViewFragment.adapter = adapter
        adapter.setOnItemClickListner(object : NewsAdapter.onItemClickListner {
            override fun onItemClick(position: Int) {
                val intent = Intent(requireContext(), DetailsActivity::class.java)
                intent.putExtra("news_url", newsList[position].url)
                startActivity(intent)
            }
        })
    }

}