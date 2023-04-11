package com.example.newapp.Activities

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import com.example.newapp.R
import com.example.newapp.databinding.ActivityDetailsBinding
import com.example.newapp.utils.Constants

class DetailsActivity : AppCompatActivity() {
    var binding: ActivityDetailsBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra(Constants.NEWS_URL)){
            val url: String? = intent.getStringExtra(Constants.NEWS_URL)
            if (url != null) {
                binding?.webView?.loadUrl(url)
                binding?.webView?.webViewClient = object: WebViewClient(){
                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        binding?.pgBar?.visibility = View.VISIBLE
                        super.onPageStarted(view, url, favicon)
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        binding?.pgBar?.visibility = View.GONE
                        super.onPageFinished(view, url)
                    }
                }
                binding?.webView?.settings?.javaScriptEnabled = true
                binding?.webView?.settings?.setSupportZoom(true)
                binding?.webView?.settings?.builtInZoomControls = true
                binding?.webView?.settings?.displayZoomControls = false
            }
        }else{
            Toast.makeText(this, "Something Went Wrong!!", Toast.LENGTH_SHORT).show()
        }

    }

    override fun onBackPressed() {

        if (binding?.webView?.canGoBack() == true){
            binding?.webView?.goBack()
        }else{
            super.onBackPressed()
        }
    }
}