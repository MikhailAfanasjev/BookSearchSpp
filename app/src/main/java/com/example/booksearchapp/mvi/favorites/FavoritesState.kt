package com.example.booksearchapp.mvi.favorites

import com.example.booksearchapp.data.remote.model.Book

/** Состояние экрана избранного */
data class FavoritesState(
    val favorites: List<Book> = emptyList(),
    val error: String? = null
)