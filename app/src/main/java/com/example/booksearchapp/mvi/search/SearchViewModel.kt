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
    private val pageSize = 20

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.SearchQuery -> {
                // Новый поиск – сбрасываем индекс и список книг
                _state.update {
                    it.copy(
                        query = intent.query,
                        isLoading = true,
                        error = null,
                        currentIndex = 0,
                        endReached = false,
                        books = emptyList()
                    )
                }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(2000L)
                    try {
                        val books = repository.searchBooks(
                            intent.query,
                            _state.value.sortOrder,
                            _state.value.author,
                            startIndex = 0,
                            maxResults = pageSize
                        )
                        _state.update { state ->
                            state.copy(
                                books = books,
                                isLoading = false,
                                currentIndex = books.size,
                                endReached = books.size < pageSize
                            )
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(
                                error = "Ошибка выполнения запроса, попробуйте повторить",
                                isLoading = false
                            )
                        }
                    }
                }
            }

            is SearchIntent.ChangeFilter -> {
                // Очищаем список книг, устанавливаем новые фильтры и сбрасываем индексы
                _state.update {
                    it.copy(
                        books = emptyList(),
                        sortOrder = intent.sortOrder,
                        author = intent.author,
                        isLoading = true,
                        currentIndex = 0,
                        endReached = false
                    )
                }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    delay(500L)
                    try {
                        val books = repository.searchBooks(
                            _state.value.query,
                            intent.sortOrder,
                            intent.author,
                            startIndex = 0,
                            maxResults = pageSize
                        )
                        _state.update { state ->
                            state.copy(
                                books = books,
                                isLoading = false,
                                currentIndex = books.size,
                                endReached = books.size < pageSize
                            )
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(
                                error = "Ошибка выполнения запроса, попробуйте повторить",
                                isLoading = false
                            )
                        }
                    }
                }
            }

            SearchIntent.Retry -> {
                onIntent(SearchIntent.SearchQuery(_state.value.query))
            }

            SearchIntent.LoadMore -> {
                // Если уже идёт загрузка или достигнут конец списка, ничего не делаем
                if (_state.value.isLoading || _state.value.endReached) return
                _state.update { it.copy(isLoading = true, error = null) }
                searchJob?.cancel()
                searchJob = viewModelScope.launch {
                    try {
                        val currentIndex = _state.value.currentIndex
                        val newBooks = repository.searchBooks(
                            _state.value.query,
                            _state.value.sortOrder,
                            _state.value.author,
                            startIndex = currentIndex,
                            maxResults = pageSize
                        )
                        _state.update { state ->
                            state.copy(
                                books = state.books + newBooks,
                                isLoading = false,
                                currentIndex = state.currentIndex + newBooks.size,
                                endReached = newBooks.size < pageSize
                            )
                        }
                    } catch (e: Exception) {
                        _state.update {
                            it.copy(
                                error = "Ошибка выполнения запроса, попробуйте повторить",
                                isLoading = false
                            )
                        }
                    }
                }
            }

            else -> {
                // Если добавятся новые варианты интентов, они будут обработаны здесь.
            }
        }
    }
}