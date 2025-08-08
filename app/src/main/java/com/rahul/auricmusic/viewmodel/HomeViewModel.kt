// In viewmodel/HomeViewModel.kt
package com.rahul.auricmusic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.auricmusic.data.AppDatabase
import com.rahul.auricmusic.data.SongRepository
import com.rahul.auricmusic.data.model.Song
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val songRepository: SongRepository

    private val _songs = MutableStateFlow<List<Song>>(emptyList())
    val songs: StateFlow<List<Song>> = _songs

    init {
        // Get an instance of the database
        val database = AppDatabase.getDatabase(application)

        // THIS IS THE CORRECTED PART:
        // Initialize the repository with all three required DAOs
        songRepository = SongRepository(
            context = application,
            favoriteSongDao = database.favoriteSongDao(),
            playlistDao = database.playlistDao(),
            songDao = database.songDao()
        )
    }

    fun loadSongs() {
        // Prevent re-loading if songs are already present
        if (_songs.value.isNotEmpty()) return

        viewModelScope.launch {
            // Switch to a background thread for the I/O operation
            val songList = withContext(Dispatchers.IO) {
                songRepository.getAudioSongs()
            }
            // Switch back to the main thread to update the UI state
            _songs.value = songList
        }
    }


    fun isFavorite(songId: Long): Flow<Boolean> {
        return songRepository.isFavorite(songId)
    }

    fun toggleFavorite(songId: Long, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            if (isCurrentlyFavorite) {
                songRepository.removeFavorite(songId)
            } else {
                songRepository.addFavorite(songId)
            }
        }
    }
}