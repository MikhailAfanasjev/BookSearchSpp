package com.example.booksearchapp.data.repository

import com.example.booksearchapp.data.local.dao.BookDao
import com.example.booksearchapp.data.local.entity.BookEntity
import com.example.booksearchapp.data.remote.model.Book
import com.example.booksearchapp.data.remote.GoogleBooksApi
import javax.inject.Inject

/** Репозиторий для получения данных */
class BooksRepository @Inject constructor(
    private val api: GoogleBooksApi,
    private val bookDao: BookDao,
    private val booksCache: MutableMap<String, Book> = mutableMapOf<String, Book>()
) {

    suspend fun searchBooks(query: String, orderBy: String? = null): List<Book> {
        val response = api.getBooks(query)
        if (response.isSuccessful) {
            return response.body()?.items?.map { item ->
                Book(
                    id = item.id,
                    title = item.volumeInfo.title,
                    authors = item.volumeInfo.authors,
                    publishedDate = item.volumeInfo.publishedDate,
                    description = item.volumeInfo.description,
                    thumbnail = item.volumeInfo.imageLinks?.thumbnail
                )
            } ?: emptyList()
        } else {
            throw Exception("Ошибка запроса: ${response.errorBody()?.string()}")
        }
    }
    suspend fun addFavorite(book: Book) {
        bookDao.insert(
            BookEntity(
                id = book.id,
                title = book.title,
                authors = book.authors?.joinToString(", ") ?: "",
                publishedDate = book.publishedDate,
                description = book.description,
                thumbnail = book.thumbnail
            )
        )
    }

    suspend fun removeFavorite(book: Book) {
        bookDao.delete(
            BookEntity(
                id = book.id,
                title = book.title,
                authors = book.authors?.joinToString(", ") ?: "",
                publishedDate = book.publishedDate,
                description = book.description,
                thumbnail = book.thumbnail
            )
        )
    }

    suspend fun getFavorites(): List<Book> {
        return bookDao.getFavorites().map { entity ->
            Book(
                id = entity.id,
                title = entity.title,
                authors = entity.authors.split(", ").filter { it.isNotBlank() },
                publishedDate = entity.publishedDate,
                description = entity.description,
                thumbnail = entity.thumbnail
            )
        }
    }

    suspend fun getBookById(bookId: String): Book {
        booksCache[bookId]?.let { return it }

        val isFavorite = bookDao.isFavorite(bookId)
        if (isFavorite) {
            bookDao.getFavorites().find { it.id == bookId }?.let { entity ->
                return Book(
                    id = entity.id,
                    title = entity.title,
                    authors = entity.authors.split(", ").filter { it.isNotBlank() },
                    publishedDate = entity.publishedDate,
                    description = entity.description,
                    thumbnail = entity.thumbnail
                ).also { booksCache[bookId] = it }
            }
        }

        val response = api.getBookById(bookId)
        if (response.isSuccessful) {
            val item = response.body() ?: throw Exception("Book not found")
            return Book(
                id = item.id,
                title = item.volumeInfo.title,
                authors = item.volumeInfo.authors,
                publishedDate = item.volumeInfo.publishedDate,
                description = item.volumeInfo.description,
                thumbnail = item.volumeInfo.imageLinks?.thumbnail
            ).also { booksCache[bookId] = it }
        } else {
            throw Exception("Ошибка загрузки книги")
        }
    }
}