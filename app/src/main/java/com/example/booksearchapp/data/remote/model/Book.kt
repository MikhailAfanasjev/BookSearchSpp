package com.example.booksearchapp.data.remote.model

/** Модель данных о книге */
data class Book(
    val id: String,
    val title: String,
    val authors: List<String>?,
    val publishedDate: String?,
    val description: String?,
    val thumbnail: String?
)
