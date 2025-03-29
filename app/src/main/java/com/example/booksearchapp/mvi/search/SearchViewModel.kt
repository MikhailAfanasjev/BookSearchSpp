package com.example.booksearchapp.mvi.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksearchapp.data.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    private var searchJob: Job? = null

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.SearchQuery -> {
                _state.update { it.copy(query = intent.query, isLoading = true, error = null) }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(2000L)
                    try {
                        val books = repository.searchBooks(
                            intent.query,
                            _state.value.sortOrder,
                            _state.value.author
                        )
                        _state.update { it.copy(books = books, isLoading = false) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = "Ошибка выполнения запроса, попробуйте повторить", isLoading = false) }
                    }
                }
            }
            is SearchIntent.ChangeFilter -> {
                _state.update { it.copy(sortOrder = intent.sortOrder, author = intent.author, isLoading = true) }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    try {
                        val books = repository.searchBooks(
                            _state.value.query,
                            intent.sortOrder,
                            intent.author
                        )
                        _state.update { it.copy(books = books, isLoading = false) }
                    } catch (e: Exception) {
                        _state.update { it.copy(error = "Ошибка выполнения запроса, попробуйте повторить", isLoading = false) }
                    }
                }
            }
            SearchIntent.Retry -> {
                onIntent(SearchIntent.SearchQuery(_state.value.query))
            }
        }
    }
}