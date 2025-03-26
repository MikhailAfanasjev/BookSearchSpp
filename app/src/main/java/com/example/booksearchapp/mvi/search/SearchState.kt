package com.example.booksearchapp.mvi.search

/** Состояние экрана поиска */
import com.example.booksearchapp.data.remote.model.Book

data class SearchState(
    val isLoading: Boolean = false,
    val books: List<Book> = emptyList(),
    val error: String? = null,
    val query: String = "",
    val sortOrder: String? = null, // "date", "author" или null для лучшего совпадения
    val author: String? = null     // Новое поле для фильтра по авторам
)