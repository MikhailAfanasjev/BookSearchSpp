package com.example.booksearchapp.presentation.screens

import android.widget.Toast
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
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
import com.example.booksearchapp.utils.showCustomToast
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
    val context = LocalContext.current

    // Запускаем эффект для показа Toast при появлении сообщения или ошибки
    LaunchedEffect(favoritesState.message, favoritesState.error) {
        favoritesState.message?.let { message ->
            showCustomToast(context, message, isError = false)
        }
        favoritesState.error?.let { error ->
            showCustomToast(context, error, isError = true)
        }
    }

    // Локальное состояние для показа фильтров
    var showFilterPanel by remember { mutableStateOf(false) }
    // Локальное состояние для ввода автора и выбора сортировки
    var authorFilter by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf<String?>(null) }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(White)
        ) {
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
                    trailingIcon = {
                        IconButton(onClick = { showFilterPanel = true }) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_filter),
                                contentDescription = "Фильтры",
                                tint = LightGray
                            )
                        }
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
                    searchState.isLoading -> {
                        // Показ индикатора загрузки
                    }
                    searchState.error != null -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = stringResource(R.string.request_execution_error),
                                    style = TextStyle(
                                        color = Black,
                                        fontSize = 18.sp,
                                        textAlign = TextAlign.Center
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                Button(
                                    onClick = { searchViewModel.onIntent(SearchIntent.Retry) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00ACFF)),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    Text(text = stringResource(R.string.try_again))
                                }
                            }
                        }
                    }
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
                                        onFavoriteClick = {
                                            favoritesViewModel.processIntent(
                                                FavoritesIntent.ToggleFavorite(book)
                                            )
                                        },
                                        onBookClick = { bookId ->
                                            navController.navigate("detail/$bookId")
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Если список пустой и нет загрузки/ошибок – сообщение для ввода запроса
                if (searchState.books.isEmpty() && !searchState.isLoading && searchState.error == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.nothing_found),
                            style = TextStyle(
                                color = Black,
                                fontSize = 18.sp,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                // Фоновое затемнение и панель фильтров
                if (showFilterPanel) {
                    // Затемнение остальной части экрана
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { showFilterPanel = false }
                    )
                    // Панель фильтров, занимающая верхнюю треть экрана
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height((configuration.screenHeightDp.dp) / 3)
                            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp))
                            .background(White)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp)
                        ) {
                            // Заголовок панели по центру
                            Text(
                                text = "Фильтры",
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Поле для ввода автора
                            Text(
                                text = "Авторы",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            TextField(
                                value = authorFilter,
                                onValueChange = { authorFilter = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                placeholder = {
                                    Text(
                                        "Введите имя автора",
                                        style = Regular_14,
                                        color = LightGray,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                },
                                textStyle = TextStyle(color = Black, fontSize = 14.sp),
                                singleLine = true,
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
                                )
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            // Надпись "Сортировать"
                            Text(
                                text = "Сортировать",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            // Две кнопки на одной строке
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(
                                    onClick = { selectedSort = "date" },
                                    modifier = Modifier.border(BorderStroke(2.dp, LightGray), shape = RoundedCornerShape(20.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = if (selectedSort == "date") MaterialTheme.colorScheme.primary else Color.Black
                                    )
                                ) {
                                    Text(text = "По дате")
                                }

                                Button(
                                    onClick = { selectedSort = "best" },
                                    modifier = Modifier.border(BorderStroke(2.dp, LightGray), shape = RoundedCornerShape(20.dp)),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = if (selectedSort == "best") MaterialTheme.colorScheme.primary else Color.Black
                                    )
                                ) {
                                    Text(text = "Лучшее совпадение")
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            // Кнопка "Применить" по центру
                            Button(
                                onClick = {
                                    searchViewModel.onIntent(
                                        SearchIntent.ChangeFilter(
                                            sortOrder = when (selectedSort) {
                                                "date" -> "date"
                                                "best" -> null
                                                else -> null
                                            },
                                            author = authorFilter
                                        )
                                    )
                                    showFilterPanel = false
                                },
                                modifier = Modifier.align(Alignment.CenterHorizontally),
                                shape = RoundedCornerShape(16.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = UltraLightGray,
                                    contentColor = Color.Black
                                )
                            ) {
                                Text(text = "Применить")
                            }
                        }
                    }
                }
            }

            // Индикатор загрузки поверх основного контента
            if (searchState.isLoading) {
                val infiniteTransition = rememberInfiniteTransition()
                val angle by infiniteTransition.animateFloat(
                    initialValue = 0f,
                    targetValue = 360f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(durationMillis = 1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_loading_spin),
                        contentDescription = "Loading",
                        modifier = Modifier
                            .size(48.dp)
                            .rotate(angle)
                    )
                }
            }
        }
    }
}