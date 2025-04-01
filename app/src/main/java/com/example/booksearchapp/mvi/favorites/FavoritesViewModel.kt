package com.example.booksearchapp.mvi.favorites

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.booksearchapp.data.remote.model.Book
import com.example.booksearchapp.data.repository.BooksRepository
import com.example.linguareader.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    application: Application,
    private val repository: BooksRepository
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(FavoritesState())
    val state: StateFlow<FavoritesState> = _state.asStateFlow()
    private val _toastEvent = MutableSharedFlow<Pair<String, Boolean>>()
    val toastEvent = _toastEvent.asSharedFlow()

    init {
        loadFavorites()
    }

    fun processIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.ToggleFavorite -> toggleFavorite(intent.book)
        }
    }

    fun loadFavorites() {
        viewModelScope.launch {
            try {
                val favorites = repository.getFavorites()
                _state.update { it.copy(favorites = favorites) }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = getApplication<Application>().getString(R.string.request_execution_error),
                        message = null
                    )
                }
            }
        }
    }

    private fun toggleFavorite(book: Book) {
        viewModelScope.launch {
            // Если книга уже в избранном – пытаемся удалить
            if (state.value.favorites.any { it.id == book.id }) {
                try {
                    repository.removeFavorite(book)
                    _state.update {
                        it.copy(
                            message = getApplication<Application>().getString(R.string.successfully_removing_favorites),
                            error = null
                        )
                    }
                    // Эмитируем событие для успешного удаления
                    _toastEvent.emit(getApplication<Application>().getString(R.string.successfully_removing_favorites) to false)
                    loadFavorites()
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            error = getApplication<Application>().getString(R.string.error_removing_favorites),
                            message = null
                        )
                    }
                    // Эмитируем событие для ошибки удаления
                    _toastEvent.emit(getApplication<Application>().getString(R.string.error_removing_favorites) to true)
                }
            } else { // Иначе – пытаемся добавить в избранное
                try {
                    repository.addFavorite(book)
                    _state.update {
                        it.copy(
                            message = getApplication<Application>().getString(R.string.successfully_added_favorites),
                            error = null
                        )
                    }
                    // Эмитируем событие для успешного добавления
                    _toastEvent.emit(getApplication<Application>().getString(R.string.successfully_added_favorites) to false)
                    loadFavorites()
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            error = getApplication<Application>().getString(R.string.error_added_favorites),
                            message = null
                        )
                    }
                    // Эмитируем событие для ошибки добавления
                    _toastEvent.emit(getApplication<Application>().getString(R.string.error_added_favorites) to true)
                }
            }
        }
    }
}