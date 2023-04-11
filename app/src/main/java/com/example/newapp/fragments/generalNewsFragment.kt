package com.example.newapp.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.newapp.Activities.DetailsActivity
import com.example.newapp.Activities.MainActivity
import com.example.newapp.Adapters.NewsAdapter
import com.example.newapp.R
import com.example.newapp.api.request_api_call_for_top_headlines
import com.example.newapp.models.Article
import com.example.newapp.models.News
import com.example.newapp.utils.Constants
import com.google.gson.Gson
import com.jama.carouselview.CarouselView
import com.jama.carouselview.enums.IndicatorAnimationType
import com.jama.carouselview.enums.OffsetType
import com.squareup.picasso.Picasso
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class generalNewsFragment : Fragment() {
    lateinit var recyclerViewFragment: RecyclerView
    lateinit var carouselView: CarouselView

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_general_news, container, false)
        recyclerViewFragment = view.findViewById(R.id.recyclerViewOfFragment)
        carouselView = view.findViewById(R.id.carouselView)

        if (internectConnectivityCheck()) {
            if (MainActivity.generalNewsData == null) {
                CoroutineScope(Dispatchers.IO).launch {
                    topHeadLinesNews(Constants.GENERAL)
                }
            } else {
                carouselViewSetUp(MainActivity.generalNewsData!!.slice(0 until 5))
                showDataToRecyclerView(MainActivity.generalNewsData!!.slice(5 until MainActivity.generalNewsData!!.size - 1))
            }
        } else {
            Toast.makeText(requireContext(), "Internet not connected", Toast.LENGTH_LONG).show()
        }


        return view
    }

    fun carouselViewSetUp(breakingNewsList: List<Article>?) {
        if (breakingNewsList == null){
            carouselView.visibility = View.GONE
            return
        }
        carouselView.apply {
            size = breakingNewsList.size
            autoPlay = true
            indicatorAnimationType = IndicatorAnimationType.THIN_WORM
            carouselOffset = OffsetType.CENTER
            setCarouselViewListener { view, position ->
                val imageView = view.findViewById<ImageView>(R.id.img_breaking_news)
                Picasso.get()
                    .load(breakingNewsList[position].urlToImage)
                    .fit()
                    .centerCrop()
                    .placeholder(R.drawable.carousel_placeholder_img)
                    .error(R.drawable.carousel_placeholder_img)
                    .into(imageView)

                val title = view.findViewById<TextView>(R.id.title_breaking_news)
                title.text = breakingNewsList[position].title

                view.setOnClickListener {
                    val intent = Intent(requireContext(), DetailsActivity::class.java)
                    intent.putExtra(Constants.NEWS_URL, breakingNewsList[position].url)
                    startActivity(intent)
                }
            }
        }
        carouselView.show()
    }

    fun internectConnectivityCheck(): Boolean {
        val connectivityManager =
            activity?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }

    fun topHeadLinesNews(category: String) {
        val retrofitBuilder = Retrofit.Builder().baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create()).build()
        val api_interface_object =
            retrofitBuilder.create(request_api_call_for_top_headlines::class.java)
        val call = api_interface_object.send_request(
            "in", category = category, Constants.API_KEY
        )
        call.enqueue(object : Callback<News> {
            override fun onResponse(call: Call<News>, response: Response<News>) {
                val json_from_response = Gson().toJson(response.body())
                val news = Gson().fromJson(json_from_response, News::class.java)
                if (news != null) {
                    MainActivity.generalNewsData = news.articles
                    carouselViewSetUp(MainActivity.generalNewsData!!.slice(0 until 5))
                    showDataToRecyclerView(news.articles.slice(5 until MainActivity.generalNewsData!!.size - 1))
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Per Day Api request limit exceeded!",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

            override fun onFailure(call: Call<News>, t: Throwable) {

            }

        })

    }


    fun showDataToRecyclerView(newsList: List<Article>?) {
        if (newsList == null) return
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