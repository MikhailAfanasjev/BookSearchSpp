package com.example.booksearchapp.mvi.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksearchapp.data.remote.model.Book
import com.example.booksearchapp.data.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookDetailViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {
    private val _book = MutableStateFlow<Book?>(null)
    val book: StateFlow<Book?> = _book.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _book.value = repository.getBookById(bookId)
                _error.value = null
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}