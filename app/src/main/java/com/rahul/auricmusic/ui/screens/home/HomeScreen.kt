// In ui/screens/home/HomeScreen.kt

package com.rahul.auricmusic.ui.screens.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rahul.auricmusic.ui.theme.AuricMusicTheme
import com.rahul.auricmusic.viewmodel.HomeViewModel
import com.rahul.auricmusic.viewmodel.MainViewModel
import com.rahul.auricmusic.data.model.Song
import com.rahul.auricmusic.ui.screens.playlists.AddToPlaylistDialog
import com.rahul.auricmusic.viewmodel.PlaylistsViewModel
@Composable
fun HomeScreen(
    homeViewModel: HomeViewModel = viewModel(),
    mainViewModel: MainViewModel = viewModel(),
    playlistsViewModel: PlaylistsViewModel = viewModel(), // <-- Get PlaylistsViewModel
    onSongClick: () -> Unit
) {
    val context = LocalContext.current

    // This version is slightly more robust for checking permission status
    val permissionToRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Manifest.permission.READ_MEDIA_AUDIO
    } else {
        Manifest.permission.READ_EXTERNAL_STORAGE
    }

    var permissionGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permissionToRequest) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
        if (isGranted) {
            homeViewModel.loadSongs()
        }
    }

    // Request permission only if not already granted
    LaunchedEffect(permissionGranted) {
        if (!permissionGranted) {
            permissionLauncher.launch(permissionToRequest)
        } else {
            homeViewModel.loadSongs()
        }
    }

    val songs by homeViewModel.songs.collectAsState()

    // --- NEW STATE FOR DIALOG ---
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

    LaunchedEffect(songs) {
        if (songs.isNotEmpty()) {
            mainViewModel.onSongListLoaded(songs)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "All Songs",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(16.dp)
        )

        if (permissionGranted) {
            if (songs.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No songs found on your device.")
                }
            } else {
                LazyColumn {
                    items(songs) { song ->
                        val isFavorite by homeViewModel.isFavorite(song.id).collectAsState(initial = false)
                        Box(modifier = Modifier.clickable {
                            mainViewModel.playSong(song)
                            onSongClick()

                        }) {
                            SongListItem(
                                song = song,
                                isFavorite = isFavorite,
                                onFavoriteClick = { currentFavStatus ->
                                    homeViewModel.toggleFavorite(song.id, currentFavStatus)
                                },
                                onAddToPlaylistClick = { songToAddToPlaylist = song }

                            )
                        }
                    }
                }
            }
        } else {
            // UI to show when permission is denied.
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    "Permission Required",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "This app needs access to your audio files to play music.",
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { permissionLauncher.launch(permissionToRequest) }) {
                    Text("Grant Permission")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    AuricMusicTheme {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Home Screen Preview")
        }
    }
}