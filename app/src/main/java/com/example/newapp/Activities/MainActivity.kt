package com.example.newapp.Activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar

import android.view.Menu
import android.view.View
//import android.widget.SearchView

import androidx.appcompat.widget.SearchView

import androidx.viewpager2.widget.ViewPager2
import com.example.newapp.Adapters.FragmentPageAdapter
import com.example.newapp.R
import com.example.newapp.api.news_api_call_for_search_query
import com.example.newapp.api.request_api_call_for_top_headlines
import com.example.newapp.databinding.ActivityMainBinding
import com.example.newapp.models.Article
import com.example.newapp.models.News
import com.example.newapp.utils.Constants
import com.google.android.material.tabs.TabLayout
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        setSupportActionBar()
        tabLayoutSetup()
    }

    fun tabLayoutSetup() {
        binding?.tabLayout?.addTab(binding?.tabLayout?.newTab()!!.setText("General"))
        binding?.tabLayout?.addTab(binding?.tabLayout?.newTab()!!.setText("Entertainment"))
        binding?.tabLayout?.addTab(binding?.tabLayout?.newTab()!!.setText("Technology"))
        binding?.tabLayout?.addTab(binding?.tabLayout?.newTab()!!.setText("Science"))
        binding?.tabLayout?.addTab(binding?.tabLayout?.newTab()!!.setText("Business"))
        binding?.tabLayout?.addTab(binding?.tabLayout?.newTab()!!.setText("Sports"))
        binding?.tabLayout?.addTab(binding?.tabLayout?.newTab()!!.setText("Health"))

        binding?.viewPager2?.adapter = FragmentPageAdapter(supportFragmentManager, lifecycle)

        binding?.tabLayout?.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null){
                    binding?.viewPager2?.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })


        binding?.viewPager2?.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding?.tabLayout?.selectTab(binding?.tabLayout?.getTabAt(position))
            }
        })
    }


    fun setSupportActionBar() {
        val toolbar = binding?.toolbar
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(false)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_24)
            actionBar.title = ""
        }
        binding?.backpressButtonToolbar?.setOnClickListener { onBackPressed() }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_item, menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView
        searchView.queryHint = "Search Worldwide..."
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null && query.isNotEmpty()) {
                    val intent = Intent(this@MainActivity, searchResultsActivity::class.java)
                    intent.apply {
                        putExtra(Constants.SEARCHED_STRING, query)
                    }
                    startActivity(intent)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }

    companion object {
        var generalNewsData: List<Article>? = null
        var entertainmentNewsData: List<Article>? = null
        var technologyNewsData: List<Article>? = null
        var scienceNewsData: List<Article>? = null
        var businessNewsData: List<Article>? = null
        var sportsNewsData: List<Article>? = null
        var healthNewsData: List<Article>? = null
    }
}