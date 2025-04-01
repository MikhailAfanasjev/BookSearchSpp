package com.example.booksearchapp.presentation.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
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
import com.example.booksearchapp.presentation.ui.theme.DarkGray
import com.example.booksearchapp.presentation.ui.theme.LightGray
import com.example.booksearchapp.presentation.ui.theme.Regular_14
import com.example.booksearchapp.presentation.ui.theme.UltraLightGray
import com.example.booksearchapp.presentation.ui.theme.White
import com.example.booksearchapp.utils.FilterChip
import com.example.booksearchapp.utils.NoRippleTheme
import com.example.booksearchapp.utils.showCustomToast
import com.example.linguareader.R

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
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

    // Слушаем события Toast через SharedFlow
    LaunchedEffect(Unit) {
        favoritesViewModel.toastEvent.collect { (message, isError) ->
            showCustomToast(context, message, isError)
        }
    }

    var showFilterPanel by remember { mutableStateOf(false) }
    var authorFilter by remember { mutableStateOf("") }
    var selectedSort by remember { mutableStateOf<String?>(null) }

    // Создаём LazyGridState для отслеживания прокрутки
    val lazyGridState = rememberLazyGridState()

    // Отслеживаем прокрутку до конца списка для подгрузки новых книг
    LaunchedEffect(lazyGridState) {
        snapshotFlow { lazyGridState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleIndex ->
                // Если индекс последнего видимого элемента близок к концу списка (например, за 4 элемента до конца), то вызываем подгрузку
                if (lastVisibleIndex != null && lastVisibleIndex >= searchState.books.size - 4) {
                    searchViewModel.onIntent(SearchIntent.LoadMore)
                }
            }
    }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(White)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Поле ввода и кнопка-фильтр
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = searchState.query,
                        onValueChange = { query ->
                            searchViewModel.onIntent(SearchIntent.SearchQuery(query))
                        },
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        placeholder = {
                            Text(
                                text = stringResource(R.string.search),
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
                            if (searchState.query.isNotEmpty()) {
                                IconButton(onClick = {
                                    searchViewModel.onIntent(SearchIntent.SearchQuery(""))
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_close),
                                        contentDescription = "Очистить поиск",
                                        tint = DarkGray
                                    )
                                }
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
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { showFilterPanel = true },
                        modifier = Modifier
                            .size(48.dp)
                            .background(UltraLightGray, shape = RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_filter),
                            contentDescription = "Фильтры",
                            tint = (if (selectedSort == "best" || selectedSort == "date" || authorFilter.isNotEmpty())
                                colorResource(R.color.blue)
                            else LightGray)
                        )
                    }
                }

                // Блок отображения выбранных фильтров
                if (selectedSort != null || authorFilter.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 8.dp, end = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        if (selectedSort == "best") {
                            FilterChip(
                                text = "Лучшее совпадение",
                                onRemove = { selectedSort = null }
                            )
                        } else if (selectedSort == "date") {
                            FilterChip(
                                text = "По дате",
                                onRemove = { selectedSort = null }
                            )
                        }
                        if (authorFilter.isNotEmpty()) {
                            FilterChip(
                                text = authorFilter,
                                onRemove = { authorFilter = "" }
                            )
                        }
                    }
                }

                // Основной контент экрана
                if (searchState.query.isEmpty()) {
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
                } else {
                    when {
                        searchState.isLoading && searchState.books.isEmpty() -> {
                            // Показ индикатора загрузки при первичной загрузке
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
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color(0xFF00ACFF)
                                        ),
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
                                state = lazyGridState,
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
                                if (searchState.isLoading) {
                                    item(span = { GridItemSpan(2) }) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(16.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            CircularProgressIndicator()
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
                }
            }

            // Оверлей с фильтрами
            if (showFilterPanel) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.5f))
                            .clickable { showFilterPanel = false }
                    )
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(
                                    bottomStart = 12.dp,
                                    bottomEnd = 12.dp
                                )
                            )
                            .clip(RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)),
                        color = White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            Text(
                                text = "Фильтры",
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.titleMedium,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
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
                                trailingIcon = {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_close),
                                        contentDescription = "Выбрать автора",
                                        tint = LightGray
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
                            Text(
                                text = "Сортировать",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                                    Button(
                                        onClick = { selectedSort = "date" },
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.border(
                                            BorderStroke(
                                                2.dp,
                                                if (selectedSort == "date") colorResource(R.color.blue) else LightGray
                                            ),
                                            shape = RoundedCornerShape(20.dp)
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text(text = stringResource(R.string.by_date))
                                    }
                                }
                                CompositionLocalProvider(LocalRippleTheme provides NoRippleTheme) {
                                    Button(
                                        onClick = { selectedSort = "best" },
                                        shape = RoundedCornerShape(20.dp),
                                        modifier = Modifier.border(
                                            BorderStroke(
                                                2.dp,
                                                if (selectedSort == "best") colorResource(R.color.blue) else LightGray
                                            ),
                                            shape = RoundedCornerShape(20.dp)
                                        ),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White,
                                            contentColor = Color.Black
                                        )
                                    ) {
                                        Text(text = stringResource(R.string.best_match))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = {
                                    searchViewModel.onIntent(
                                        SearchIntent.ChangeFilter(
                                            sortOrder = when (selectedSort) {
                                                "date" -> "date"
                                                "best" -> "best"
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
                                    containerColor = if (selectedSort != null || authorFilter.isNotEmpty()) {
                                        colorResource(id = R.color.blue)
                                    } else {
                                        colorResource(id = R.color.ultra_light_gray)
                                    },
                                    contentColor = if (selectedSort != null || authorFilter.isNotEmpty()) Color.White else Color.Black
                                )
                            ) {
                                Text(text = stringResource(R.string.apply))
                            }
                        }
                    }
                }
            }
        }

        // Индикатор загрузки поверх основного контента
        if (searchState.isLoading && searchState.books.isEmpty()) {
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