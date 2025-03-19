package com.example.booksearchapp.mvi.favorites

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksearchapp.data.remote.model.Book
import com.example.booksearchapp.data.repository.BooksRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/** ViewModel для поиска */
@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val repository: BooksRepository
) : ViewModel() {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()

    init {
        loadFavorites()
    }

    fun processIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.ToggleFavorite -> toggleFavorite(intent.book)
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favorites = repository.getFavorites()
                _state.update { it.copy(favorites = favorites) }
            } catch (e: Exception) {
                // Обработка ошибки загрузки
            }
        }
    }

    private fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            try {
                // Используем прямой запрос к DAO через репозиторий
                val isFavorite = repository.isFavorite(book.id) // <-- Важное изменение!

                if (isFavorite) {
                    repository.removeFavorite(book)
                    _state.update { currentState ->
                        currentState.copy(favorites = currentState.favorites - book)
                    }
                } else {
                    repository.addFavorite(book)
                    _state.update { currentState ->
                        currentState.copy(favorites = currentState.favorites + book)
                    }
                }
            } catch (e: Exception) {
                Log.e("FAVORITES", "Error toggling favorite", e)
            }
        }
    }
    fun getBookById(bookId: String): Book? {
        return state.value.favorites.firstOrNull { it.id == bookId }
    }
}