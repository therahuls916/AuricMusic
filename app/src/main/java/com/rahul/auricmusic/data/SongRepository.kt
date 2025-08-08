// In data/SongRepository.kt
package com.rahul.auricmusic.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import com.rahul.auricmusic.data.model.FavoriteSong
import com.rahul.auricmusic.data.model.Playlist
import com.rahul.auricmusic.data.model.PlaylistSongCrossRef
import com.rahul.auricmusic.data.model.Song
import kotlinx.coroutines.flow.Flow

class SongRepository(
    private val context: Context,
    private val favoriteSongDao: FavoriteSongDao,
    private val playlistDao: PlaylistDao,
    private val songDao: SongDao
) {


    fun getAudioSongs(): List<Song> {
        val songs = mutableListOf<Song>()


        val minDurationMs = 60000

        val collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION
        )


        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= ?"
        val selectionArgs = arrayOf(minDurationMs.toString())


        context.contentResolver.query(
            collection,
            projection,
            selection, // Use the new selection
            selectionArgs, // Provide the value for the '?' placeholder
            "${MediaStore.Audio.Media.TITLE} ASC" // Sort alphabetically by title
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val albumIdColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
            // No need to get the duration again, we just used it for filtering

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val artist = cursor.getString(artistColumn)
                val albumId = cursor.getLong(albumIdColumn)

                val songUri = ContentUris.withAppendedId(
                    collection,
                    id
                ).toString()

                val albumArtUri = ContentUris.withAppendedId(
                    android.net.Uri.parse("content://media/external/audio/albumart"),
                    albumId
                ).toString()

                songs.add(Song(id, title, artist, songUri, albumArtUri))
            }
        }
        return songs
    }

    // New functions for favorites
    fun isFavorite(songId: Long): Flow<Boolean> = favoriteSongDao.isFavorite(songId)

    fun getFavoriteSongs(): Flow<List<FavoriteSong>> = favoriteSongDao.getAllFavorites()

    suspend fun addFavorite(songId: Long) {
        favoriteSongDao.addFavorite(FavoriteSong(id = songId))
    }

    suspend fun removeFavorite(songId: Long) {
        favoriteSongDao.removeFavorite(FavoriteSong(id = songId))
    }

    suspend fun addSongToPlaylist(song: Song, playlistId: Long) {
        // First, ensure the song exists in our 'songs' table.
        // OnConflictStrategy.IGNORE means if it's already there, nothing happens.
        songDao.insertSong(song)
        // Now, create the link in our cross-reference table.
        playlistDao.addSongToPlaylist(
            PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = song.id
            )
        )
    }

    suspend fun removeSongFromPlaylist(songId: Long, playlistId: Long) {
        playlistDao.deleteSongFromPlaylist(
            PlaylistSongCrossRef(playlistId = playlistId, songId = songId)
        )
    }

    suspend fun deletePlaylist(playlist: Playlist) {
        playlistDao.deletePlaylist(playlist)
    }

}
