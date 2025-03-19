package com.example.booksearchapp.mvi.favorites

import com.example.booksearchapp.data.remote.model.Book

/** Состояние экрана поиска */
data class FavoritesState(
    val favorites: List<Book> = emptyList()
)