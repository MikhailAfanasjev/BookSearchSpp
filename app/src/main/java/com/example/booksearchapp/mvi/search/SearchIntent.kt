package com.example.booksearchapp.mvi.search

/** Интент для избранного */
sealed class SearchIntent {
    data class SearchQuery(val query: String) : SearchIntent()
    object Retry : SearchIntent()
}