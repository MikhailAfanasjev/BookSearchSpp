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

/** ViewModel для избранного */
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

    // Сделали функцию публичной
    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favorites = repository.getFavorites()
                Log.d("FavoritesViewModel", "Favorites loaded: ${favorites.size}")
                _state.update { it.copy(favorites = favorites) }
            } catch (e: Exception) {
                Log.e("FavoritesViewModel", "Error loading favorites", e)
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    private fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            try {
                if (state.value.favorites.any { it.id == book.id }) {
                    repository.removeFavorite(book)
                } else {
                    repository.addFavorite(book)
                }
                loadFavorites()
            } catch (e: Exception) {
                _state.update { it.copy(error = e.message) }
            }
        }
    }
}