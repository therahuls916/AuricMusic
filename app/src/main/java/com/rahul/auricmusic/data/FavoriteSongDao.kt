// In data/FavoriteSongDao.kt
package com.rahul.auricmusic.data

import androidx.room.*
import com.rahul.auricmusic.data.model.FavoriteSong
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteSongDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavorite(favoriteSong: FavoriteSong)

    @Delete
    suspend fun removeFavorite(favoriteSong: FavoriteSong)

    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteSong>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE id = :songId LIMIT 1)")
    fun isFavorite(songId: Long): Flow<Boolean>
}