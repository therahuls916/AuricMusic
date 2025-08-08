// In viewmodel/FavoritesViewModel.kt
package com.rahul.auricmusic.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.rahul.auricmusic.data.AppDatabase
import com.rahul.auricmusic.data.SongRepository
import com.rahul.auricmusic.data.model.Song
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val songRepository: SongRepository

    val favoriteSongs: StateFlow<List<Song>>

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

        // The rest of the logic for combining flows remains the same.
        // It's powerful because it fetches all songs from storage, and then filters
        // them based on the favorite IDs it gets from the database.
        val allSongs = songRepository.getAudioSongs()
        val favoriteSongIdsFlow = songRepository.getFavoriteSongs()

        favoriteSongs = combine(
            favoriteSongIdsFlow
        ) { (favoriteIds) ->
            val favoriteIdSet = favoriteIds.map { it.id }.toSet()
            allSongs.filter { it.id in favoriteIdSet }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }
}