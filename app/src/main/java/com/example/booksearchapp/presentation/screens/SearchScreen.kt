package com.example.booksearchapp.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.booksearchapp.mvi.favorites.FavoritesIntent
import com.example.booksearchapp.mvi.favorites.FavoritesViewModel
import com.example.booksearchapp.mvi.search.SearchIntent
import com.example.booksearchapp.mvi.search.SearchViewModel
import com.example.booksearchapp.presentation.components.BookItem
import com.example.booksearchapp.presentation.ui.theme.Black
import com.example.booksearchapp.presentation.ui.theme.LightGray
import com.example.booksearchapp.presentation.ui.theme.Regular_14
import com.example.booksearchapp.presentation.ui.theme.UltraLightGray
import com.example.booksearchapp.presentation.ui.theme.White
import com.example.linguareader.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    navController: NavHostController,
    searchViewModel: SearchViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel(),
    onBookClick: (bookId: String) -> Unit = {}
) {
    val searchState by searchViewModel.state.collectAsState()
    val favoritesState by favoritesViewModel.state.collectAsState()
    val configuration = LocalConfiguration.current

    Box(modifier = Modifier.fillMaxSize().background(White)) {
        Column(modifier = Modifier.fillMaxSize()) {
            TextField(
                value = searchState.query,
                onValueChange = { query ->
                    searchViewModel.onIntent(SearchIntent.SearchQuery(query))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                placeholder = {
                    Text(
                        stringResource(R.string.search),
                        style = Regular_14,
                        color = LightGray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_search),
                        contentDescription = null,
                        tint = LightGray
                    )
                },
                textStyle = TextStyle(color = Black, fontSize = 14.sp),
                singleLine = true,
                maxLines = 1,
                shape = RoundedCornerShape(12.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = UltraLightGray,
                    unfocusedContainerColor = UltraLightGray,
                    disabledContainerColor = LightGray,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black,
                    cursorColor = Color.Black
                ),
                keyboardActions = KeyboardActions(onSearch = {
                    searchViewModel.onIntent(SearchIntent.SearchQuery(searchState.query))
                })
            )

            when {
                searchState.isLoading -> { /* ... */ }
                searchState.error != null -> { /* ... */ }
                searchState.books.isNotEmpty() -> {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp)
                    ) {
                        items(
                            count = searchState.books.size,
                            key = { index -> searchState.books[index].id }
                        ) { index ->
                            val book = searchState.books[index]
                            val itemHeight = configuration.screenHeightDp.dp * 0.50f

                            Box(
                                modifier = Modifier
                                    .height(itemHeight)
                                    .padding(4.dp)
                            ) {
                                BookItem(
                                    book = book,
                                    isFavorite = favoritesState.favorites.any { it.id == book.id },
                                    onFavoriteClick = { favoritesViewModel.processIntent(FavoritesIntent.ToggleFavorite(book)) },
                                    onBookClick = { bookId ->
                                        navController.navigate("detail/$bookId")
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (searchState.books.isEmpty() && !searchState.isLoading && searchState.error == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.еnter_title_book),
                    style = TextStyle(
                        color = Black,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}