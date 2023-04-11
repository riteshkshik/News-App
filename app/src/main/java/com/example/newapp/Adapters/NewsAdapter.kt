package com.example.newapp.Adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.example.newapp.R
import com.example.newapp.models.Article
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class NewsAdapter(val context: Context, val newsList: List<Article>): RecyclerView.Adapter<NewsAdapter.MyViewHolder>() {


    private lateinit var mListner: onItemClickListner
    interface onItemClickListner{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListner(listner: onItemClickListner){
        mListner = listner
    }


    inner class MyViewHolder(itemView: View, listner: onItemClickListner): RecyclerView.ViewHolder(itemView){
        var title = itemView.findViewById<TextView>(R.id.text_title)
        var published_date = itemView.findViewById<TextView>(R.id.published_date)
        var img = itemView.findViewById<ImageView>(R.id.img_headline)
        init {
            itemView.setOnClickListener {
                listner.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.ui_for_recyclerview_item, parent, false)
        return MyViewHolder(view, mListner)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.title.text = newsList[position].title
        val imageUrl = newsList[position].urlToImage
        val date = newsList[position].publishedAt
        if (date.isNullOrEmpty()){
            holder.published_date.text = ""
        }else{

            val then = run {
                val oldDate = LocalDate.parse(date.substring(0,10), DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                val oldTime = LocalTime.parse(date.substring(11,16), DateTimeFormatter.ofPattern("HH:mm"))
                oldDate.atTime(oldTime)
            }
            val now = LocalDateTime.now()
            val hours = then.until(now, ChronoUnit.HOURS)
            val days = then.until(now, ChronoUnit.DAYS)

            if (hours <= 48){
                holder.published_date.text = "${hours} hrs ago"
            }else{
                holder.published_date.text = "${days} days ago"
            }


        }
        if (imageUrl.isNullOrEmpty()){
            holder.img.setImageResource(R.drawable.carousel_placeholder_img)
        }else{
            Glide.with(holder.itemView.context)
                .load(newsList[position].urlToImage)
                .thumbnail(0.05f)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(holder.img)
        }


    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}