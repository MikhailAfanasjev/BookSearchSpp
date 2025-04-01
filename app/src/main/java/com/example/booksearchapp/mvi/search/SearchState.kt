package com.example.booksearchapp.mvi.search

/** Состояние экрана поиска */
import com.example.booksearchapp.data.remote.model.Book

data class SearchState(
    val query: String = "",
    val books: List<Book> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val sortOrder: String? = null,
    val author: String? = null,
    val currentIndex: Int = 0, // Текущий стартовый индекс
    val endReached: Boolean = false // Флаг, что больше книг нет
)