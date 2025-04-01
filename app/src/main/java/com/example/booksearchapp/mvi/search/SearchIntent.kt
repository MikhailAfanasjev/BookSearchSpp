package com.example.booksearchapp.mvi.search

/** Интент для поиска */
sealed class SearchIntent {
    data class SearchQuery(val query: String) : SearchIntent()
    data class ChangeFilter(val sortOrder: String?, val author: String?) : SearchIntent()
    object Retry : SearchIntent()
    object LoadMore : SearchIntent() // Новый интент для подгрузки следующей страницы
}