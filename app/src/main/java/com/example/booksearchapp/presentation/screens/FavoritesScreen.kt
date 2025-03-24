package com.example.booksearchapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.booksearchapp.mvi.favorites.FavoritesIntent
import com.example.booksearchapp.mvi.favorites.FavoritesViewModel
import com.example.booksearchapp.presentation.components.BookItem
import com.example.booksearchapp.presentation.ui.theme.Black
import com.example.booksearchapp.presentation.ui.theme.White

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    navController: NavHostController,
    onBookClick: (String) -> Unit,
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    // При входе на экран загружаем избранные книги
    LaunchedEffect(Unit) {
        favoritesViewModel.loadFavorites()
    }

    // Получаем актуальное состояние
    val favoritesState by favoritesViewModel.state.collectAsState()
    // Получаем конфигурацию экрана для расчёта высоты элемента
    val configuration = LocalConfiguration.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        if (favoritesState.favorites.isEmpty()) {
            // Отображение сообщения, если список пуст
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Нет избранных книг",
                    style = TextStyle(
                        color = Black,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                )
            }
        } else {
            // Отображаем список избранных книг аналогично SearchScreen
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                items(
                    count = favoritesState.favorites.size,
                    key = { index -> favoritesState.favorites[index].id }
                ) { index ->
                    val book = favoritesState.favorites[index]
                    // Вычисляем высоту элемента (50% высоты экрана)
                    val itemHeight = configuration.screenHeightDp.dp * 0.50f

                    Box(
                        modifier = Modifier
                            .height(itemHeight)
                            .padding(4.dp)
                    ) {
                        BookItem(
                            book = book,
                            isFavorite = true, // Все книги на этом экране уже избранные
                            onFavoriteClick = {
                                favoritesViewModel.processIntent(FavoritesIntent.ToggleFavorite(book))
                            },
                            onBookClick = { bookId ->
                                // Навигация на экран деталей
                                navController.navigate("detail/$bookId")
                            }
                        )
                    }
                }
            }
        }
    }
}