package com.example.booksearchapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.booksearchapp.data.local.entity.BookEntity

@Dao
interface BookDao {
    @Query("SELECT * FROM favorite_books")
    suspend fun getFavorites(): List<BookEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(book: BookEntity)

    @Delete
    suspend fun delete(book: BookEntity)

    @Query("SELECT EXISTS(SELECT * FROM favorite_books WHERE id = :bookId)")
    suspend fun isFavorite(bookId: String): Boolean
}