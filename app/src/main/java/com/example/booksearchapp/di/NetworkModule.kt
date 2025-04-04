package com.example.booksearchapp.di

import android.content.Context
import com.example.booksearchapp.data.local.AppDatabase
import com.example.booksearchapp.data.local.dao.BookDao
import com.example.booksearchapp.data.remote.GoogleBooksApi
import com.example.booksearchapp.data.repository.BooksRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

/** Модуль для предоставления Retrofit, API и репозитория */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val BASE_URL = "https://www.googleapis.com/books/v1/"

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideGoogleBooksApi(retrofit: Retrofit): GoogleBooksApi {
        return retrofit.create(GoogleBooksApi::class.java)
    }

    @Provides
    @Singleton
    fun provideBooksRepository(api: GoogleBooksApi, bookDao: BookDao): BooksRepository {
        return BooksRepository(api, bookDao)
    }
    @Provides
    @Singleton
    fun provideBookDao(@ApplicationContext appContext: Context): BookDao {
        return AppDatabase.getDatabase(appContext).bookDao()
    }
}