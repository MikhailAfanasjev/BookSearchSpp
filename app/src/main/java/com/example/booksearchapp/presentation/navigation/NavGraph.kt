package com.example.booksearchapp.presentation.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.booksearchapp.mvi.favorites.FavoritesViewModel
import com.example.booksearchapp.mvi.search.SearchViewModel
import com.example.booksearchapp.presentation.screens.BookDetailScreen
import com.example.booksearchapp.presentation.screens.FavoritesScreen
import com.example.booksearchapp.presentation.screens.SearchScreen

@Composable
fun NavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = "search",
        modifier = modifier
    ) {
        composable("search") {
            SearchScreen(navController = navController)
        }
        composable("favorites") {
            FavoritesScreen(onBookClick = { bookId ->
                navController.navigate("detail/$bookId")
            })
        }
        composable("detail/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId") ?: ""
            val searchViewModel: SearchViewModel = hiltViewModel()
            val favoritesViewModel: FavoritesViewModel = hiltViewModel()

            // Загрузка данных о книге по bookId
            val book = searchViewModel.getBookById(bookId)
                ?: favoritesViewModel.getBookById(bookId)

            if (book != null) {
                BookDetailScreen(book = book)
            } else {
                // Обработка случая, когда книга не найдена
                Text("Информация о книге недоступна")
            }
        }
    }
}