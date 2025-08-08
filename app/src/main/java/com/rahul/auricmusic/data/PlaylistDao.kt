// In data/PlaylistDao.kt
package com.rahul.auricmusic.data

import androidx.room.*
import com.rahul.auricmusic.data.model.Playlist
import com.rahul.auricmusic.data.model.PlaylistSongCrossRef
import com.rahul.auricmusic.data.model.PlaylistWithSongs
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Transaction
    @Query("SELECT * FROM playlists")
    fun getAllPlaylistsWithSongs(): Flow<List<PlaylistWithSongs>>

    @Delete
    suspend fun deleteSongFromPlaylist(crossRef: PlaylistSongCrossRef) // <-- ADD THIS


    @Transaction
    @Query("SELECT * FROM playlists WHERE id = :playlistId")
    fun getPlaylistWithSongs(playlistId: Long): Flow<PlaylistWithSongs> // <-- ADD THIS

    @Delete
    suspend fun deletePlaylist(playlist: Playlist) // <-- ADD THIS


}
