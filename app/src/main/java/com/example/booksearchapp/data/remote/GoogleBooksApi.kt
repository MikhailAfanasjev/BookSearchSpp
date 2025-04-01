package com.example.booksearchapp.data.remote

import com.example.booksearchapp.data.remote.model.BookItem
import com.example.booksearchapp.data.remote.model.BooksResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/** API-интерфейс для Google Books API */
interface GoogleBooksApi {
    @GET("volumes")
    suspend fun getBooks(
        @Query("q") query: String,
        @Query("orderBy") orderBy: String? = null,
        @Query("startIndex") startIndex: Int = 0,
        @Query("maxResults") maxResults: Int = 20
    ): Response<BooksResponse>

    @GET("volumes/{bookId}")
    suspend fun getBookById(
        @Path("bookId") bookId: String
    ): Response<BookItem>
}