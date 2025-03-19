package com.example.booksearchapp.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.booksearchapp.data.remote.model.Book
import com.example.booksearchapp.presentation.ui.theme.Bold_18
import com.example.booksearchapp.presentation.ui.theme.Regular_14
import com.example.booksearchapp.presentation.ui.theme.Regular_16
import com.example.linguareader.R

@Composable
fun BookDetailScreen(book: Book) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        AsyncImage(
            model = book.thumbnail,
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = book.title, style = Bold_18)
        Text(text = book.authors?.joinToString(", ") ?: "", style = Regular_16)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = stringResource(R.string.published_date, book.publishedDate ?: "Не указана"),
            style = Regular_14
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = book.description ?: stringResource(R.string.no_description),
            style = Regular_16
        )
    }
}