package com.example.booksearchapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_books")
data class BookEntity(
    @PrimaryKey val id: String,
    val title: String,
    val authors: String,
    val publishedDate: String?,
    val description: String?,
    val thumbnail: String?
)