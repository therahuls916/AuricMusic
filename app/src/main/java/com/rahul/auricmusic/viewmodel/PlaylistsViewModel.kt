// In viewmodel/PlaylistsViewModel.kt
package com.rahul.auricmusic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.auricmusic.data.AppDatabase
import com.rahul.auricmusic.data.model.Playlist
import com.rahul.auricmusic.data.model.PlaylistWithSongs
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import com.rahul.auricmusic.data.SongRepository // <-- ADD IMPORT
import com.rahul.auricmusic.data.model.Song // <-- ADD IMPORT

class PlaylistsViewModel(application: Application) : AndroidViewModel(application) {

    private val playlistDao = AppDatabase.getDatabase(application).playlistDao()

    // --- NEW LOGIC ---
    private val songRepository: SongRepository

    init {
        val db = AppDatabase.getDatabase(application)
        songRepository = SongRepository(application, db.favoriteSongDao(), db.playlistDao(), db.songDao())
    }
    // --- END OF NEW LOGIC ---

    // This flow will automatically update when playlists change.
    val playlists: StateFlow<List<PlaylistWithSongs>> = playlistDao.getAllPlaylistsWithSongs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun createNewPlaylist(name: String) {
        viewModelScope.launch {
            playlistDao.insertPlaylist(Playlist(name = name))
        }
    }

    // --- NEW FUNCTION ---
    fun addSongToPlaylist(song: Song, playlistId: Long) {
        viewModelScope.launch {
            songRepository.addSongToPlaylist(song, playlistId)
        }
    }
    // --- END OF NEW FUNCTION ---

    // --- NEW FUNCTION ---
    fun removeSongFromPlaylist(songId: Long, playlistId: Long) {
        viewModelScope.launch {
            songRepository.removeSongFromPlaylist(songId, playlistId)
        }
    }
    // --- END OF NEW FUNCTION ---

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            // Note: We need to pass the actual Playlist object, not just the ID.
            songRepository.deletePlaylist(playlist)
        }
    }
}