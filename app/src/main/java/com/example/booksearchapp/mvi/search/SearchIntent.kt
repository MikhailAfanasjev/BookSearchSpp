package com.example.booksearchapp.mvi.search

/** Интент для поиска */
sealed class SearchIntent {
    data class SearchQuery(val query: String) : SearchIntent()
    data class ChangeFilter(val sortOrder: String?, val author: String?) : SearchIntent() // Новый интент
    object Retry : SearchIntent()
}