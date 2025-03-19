package com.example.booksearchapp.mvi.favorites

import com.example.booksearchapp.data.remote.model.Book

sealed class FavoritesIntent {
    data class ToggleFavorite(val book: Book) : FavoritesIntent()
}