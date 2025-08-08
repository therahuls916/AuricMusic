// In ui/screens/playlists/PlaylistDetailsScreen.kt
package com.rahul.auricmusic.ui.screens.playlists

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.auricmusic.data.AppDatabase
import com.rahul.auricmusic.data.PlaylistDao
import com.rahul.auricmusic.data.model.PlaylistWithSongs
import com.rahul.auricmusic.data.model.Song
import com.rahul.auricmusic.ui.screens.home.SongListItem
import com.rahul.auricmusic.viewmodel.HomeViewModel
import com.rahul.auricmusic.viewmodel.MainViewModel
import com.rahul.auricmusic.viewmodel.PlaylistsViewModel
import kotlinx.coroutines.flow.Flow

class PlaylistDetailsViewModel(
    playlistDao: PlaylistDao,
    val playlistId: Long
) : ViewModel() {
    val playlistWithSongs: Flow<PlaylistWithSongs> = playlistDao.getPlaylistWithSongs(playlistId)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailsScreen(
    playlistId: Long,
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel,
    playlistsViewModel: PlaylistsViewModel,
    onBackClick: () -> Unit,
    onSongClick: () -> Unit
) {
    val context = LocalContext.current
    val detailsViewModel: PlaylistDetailsViewModel = viewModel(
        key = "playlist_details_$playlistId",
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                val db = AppDatabase.getDatabase(context)
                @Suppress("UNCHECKED_CAST")
                return PlaylistDetailsViewModel(db.playlistDao(), playlistId) as T
            }
        }
    )

    val playlistWithSongs by detailsViewModel.playlistWithSongs.collectAsState(initial = null)

    // State for the "Add to another playlist" dialog
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    val allPlaylists by playlistsViewModel.playlists.collectAsState()

    if (songToAddToPlaylist != null) {
        AddToPlaylistDialog(
            playlists = allPlaylists.filter { it.playlist.id != playlistId },
            onDismiss = { songToAddToPlaylist = null },
            onPlaylistSelected = { newPlaylistId ->
                songToAddToPlaylist?.let { song ->
                    playlistsViewModel.addSongToPlaylist(song, newPlaylistId)
                }
                songToAddToPlaylist = null
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(playlistWithSongs?.playlist?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            val songs = playlistWithSongs?.songs

            if (songs == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (songs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("This playlist is empty. Add songs from the Home screen.")
                }
            } else {
                LazyColumn {
                    items(songs) { song ->
                        val isFavorite by homeViewModel.isFavorite(song.id).collectAsState(initial = false)
                        Box(modifier = Modifier.clickable {
                            mainViewModel.playSong(song)
                            onSongClick()
                        }) {
                            // This is the final, correct implementation
                            SongListItem(
                                song = song,
                                isFavorite = isFavorite,
                                onFavoriteClick = { favStatus -> homeViewModel.toggleFavorite(song.id, favStatus) },
                                // The "more" button's action is to add to ANOTHER playlist
                                onAddToPlaylistClick = { songToAddToPlaylist = song },
                                // We provide the "remove" lambda, which makes the Delete icon appear
                                onRemoveFromPlaylistClick = {
                                    playlistsViewModel.removeSongFromPlaylist(song.id, playlistId)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}