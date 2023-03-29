package com.example.newapp.models

data class News(
    val articles: List<Article>,
    val status: String,
    val totalResults: Int
)