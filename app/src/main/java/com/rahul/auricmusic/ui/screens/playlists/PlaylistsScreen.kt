// In ui/screens/playlists/PlaylistsScreen.kt
package com.rahul.auricmusic.ui.screens.playlists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.rahul.auricmusic.R
import com.rahul.auricmusic.data.model.Playlist
import com.rahul.auricmusic.data.model.PlaylistWithSongs
import com.rahul.auricmusic.ui.theme.AuricMusicTheme
import com.rahul.auricmusic.ui.theme.Goldenrod
import com.rahul.auricmusic.viewmodel.PlaylistsViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlaylistsScreen(
    playlistsViewModel: PlaylistsViewModel = viewModel(),
    onPlaylistClick: (Long) -> Unit
) {
    val playlists by playlistsViewModel.playlists.collectAsState()
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var playlistToDelete by remember { mutableStateOf<Playlist?>(null) }

    // --- Confirmation Dialog for Deleting a Playlist ---
    playlistToDelete?.let { playlist ->
        AlertDialog(
            onDismissRequest = { playlistToDelete = null },
            title = { Text("Delete Playlist") },
            text = { Text("Are you sure you want to delete the playlist '${playlist.name}'? This cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        playlistsViewModel.deletePlaylist(playlist)
                        playlistToDelete = null
                    }
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { playlistToDelete = null }) { Text("Cancel") }
            }
        )
    }

    // --- Dialog for Creating a New Playlist ---
    if (showCreatePlaylistDialog) {
        CreatePlaylistDialog(
            onDismiss = { showCreatePlaylistDialog = false },
            onCreate = { playlistName ->
                playlistsViewModel.createNewPlaylist(playlistName)
                showCreatePlaylistDialog = false
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreatePlaylistDialog = true },
                containerColor = Goldenrod,
                contentColor = Color.Black
            ) {
                Icon(Icons.Filled.Add, contentDescription = "Create Playlist")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Create Playlist", fontWeight = FontWeight.Bold)
            }
        },
        floatingActionButtonPosition = FabPosition.Center
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(
                text = "My Playlists",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (playlists.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(
                        "You haven't created any playlists yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    items(playlists) { playlistWithSongs ->
                        // The PlaylistCard itself is the UI, the gestures are handled by the modifier here.
                        PlaylistCard(
                            playlistWithSongs = playlistWithSongs,
                            modifier = Modifier.combinedClickable(
                                onClick = { onPlaylistClick(playlistWithSongs.playlist.id) },
                                onLongClick = { playlistToDelete = playlistWithSongs.playlist }
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PlaylistCard(
    playlistWithSongs: PlaylistWithSongs,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
    ) {
        Box(modifier = Modifier.height(150.dp)) {

            // 1. Determine which image to show.
            val imageModel = if (playlistWithSongs.songs.isNotEmpty()) {
                // If the playlist has songs, use the album art of the first song.
                playlistWithSongs.songs.first().albumArtUri
            } else {
                // Otherwise, use the placeholder icon.
                R.drawable.logo_icon_transparent
            }

            Image(
                // 2. Use the dynamic imageModel here.
                //    Use Coil to handle loading the URI or the drawable resource.
                painter = rememberAsyncImagePainter(
                    model = imageModel,
                    // Show the placeholder if the song's album art can't be loaded.
                    error = painterResource(id = R.drawable.logo_icon_transparent)
                ),

                contentDescription = playlistWithSongs.playlist.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Crop will make it fill the box nicely.
            )
            // The rest of the composable stays the same...
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Bottom
            ) {
                Text(
                    text = playlistWithSongs.playlist.name,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "${playlistWithSongs.songs.size} songs",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 14.sp
                )
            }
        }
    }
}


// Add this entire function to your PlaylistsScreen.kt file

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePlaylistDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var playlistName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(shape = RoundedCornerShape(16.dp)) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("New Playlist", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = playlistName,
                    onValueChange = { playlistName = it },
                    label = { Text("Playlist Name") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = { onCreate(playlistName) },
                        // The button is only enabled if the user has typed a name
                        enabled = playlistName.isNotBlank()
                    ) {
                        Text("Create")
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PlaylistsScreenPreview() {
    AuricMusicTheme {
        PlaylistsScreen(onPlaylistClick = {})
    }
}