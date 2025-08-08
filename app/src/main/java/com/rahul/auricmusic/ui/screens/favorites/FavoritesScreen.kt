// In ui/screens/favorites/FavoritesScreen.kt
package com.rahul.auricmusic.ui.screens.favorites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.auricmusic.data.model.Song
import com.rahul.auricmusic.ui.screens.home.SongListItem
import com.rahul.auricmusic.ui.screens.playlists.AddToPlaylistDialog
import com.rahul.auricmusic.viewmodel.FavoritesViewModel
import com.rahul.auricmusic.viewmodel.HomeViewModel
import com.rahul.auricmusic.viewmodel.MainViewModel
import com.rahul.auricmusic.viewmodel.PlaylistsViewModel

@Composable
fun FavoritesScreen(
    favoritesViewModel: FavoritesViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel(),
    playlistsViewModel: PlaylistsViewModel = viewModel(), // <-- Get PlaylistsViewModel
    mainViewModel: MainViewModel = viewModel(), // Add MainViewModel here
    onSongClick: () -> Unit
) {
    val favoriteSongs by favoritesViewModel.favoriteSongs.collectAsState()
// --- NEW STATE FOR DIALOG (Identical to HomeScreen) ---
    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    val playlists by playlistsViewModel.playlists.collectAsState()

    if (songToAddToPlaylist != null) {
        AddToPlaylistDialog(
            playlists = playlists,
            onDismiss = { songToAddToPlaylist = null },
            onPlaylistSelected = { playlistId ->
                songToAddToPlaylist?.let { song ->
                    playlistsViewModel.addSongToPlaylist(song, playlistId)
                }
                songToAddToPlaylist = null
            }
        )
    }
    // --- END OF NEW STATE ---
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Favorites",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (favoriteSongs.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "You haven't favorite any songs yet.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn {
                items(favoriteSongs) { song ->
                    // THIS IS THE FIX: Wrap the item in a clickable Box
                    Box(modifier = Modifier.clickable {
                        mainViewModel.playSong(song)
                        onSongClick()
                    }) {
                        SongListItem(
                            song = song,
                            isFavorite = true, // All songs here are favorites
                            onFavoriteClick = {
                                homeViewModel.toggleFavorite(song.id, isCurrentlyFavorite = true)
                            },
                            // When "More" is clicked, set the song to be added
                            onAddToPlaylistClick = { songToAddToPlaylist = song }
                        )
                    }
                }
            }
        }
    }
}