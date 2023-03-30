package com.example.newapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newapp.models.Article

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
        //var source = itemView.findViewById<TextView>(R.id.text_source)
        var img = itemView.findViewById<ImageView>(R.id.img_headline)
        init {
            itemView.setOnClickListener {
                listner.onItemClick(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsAdapter.MyViewHolder {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.new_ui_for_newslist, parent, false)
        return MyViewHolder(view, mListner)
    }

    override fun onBindViewHolder(holder: NewsAdapter.MyViewHolder, position: Int) {
        holder.title.text = newsList[position].title
        //holder.source.text = newsList[position].source.name
        Glide.with(holder.itemView.context).load(newsList[position].urlToImage).into(holder.img)
    }

    override fun getItemCount(): Int {
        return newsList.size
    }
}