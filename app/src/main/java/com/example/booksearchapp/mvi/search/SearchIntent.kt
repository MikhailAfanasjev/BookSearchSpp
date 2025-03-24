package com.example.booksearchapp.mvi.search

/** Интент для поиска */
sealed class SearchIntent {
    data class SearchQuery(val query: String) : SearchIntent()
    object Retry : SearchIntent()
}