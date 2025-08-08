// In ui/screens/playlists/AddToPlaylistDialog.kt
package com.rahul.auricmusic.ui.screens.playlists

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rahul.auricmusic.R
import com.rahul.auricmusic.data.model.PlaylistWithSongs

@Composable
fun AddToPlaylistDialog(
    playlists: List<PlaylistWithSongs>,
    onDismiss: () -> Unit,
    onPlaylistSelected: (Long) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.heightIn(max = 500.dp) // Set a max height for long lists
        ) {
            Column {
                Text(
                    text = "Add to playlist",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(24.dp)
                )

                // The list of playlists
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false) // Allow the column to scroll within the dialog
                        .padding(horizontal = 8.dp)
                ) {
                    items(playlists) { playlistWithSongs ->
                        DialogPlaylistItem(
                            playlistWithSongs = playlistWithSongs,
                            onClick = { onPlaylistSelected(playlistWithSongs.playlist.id) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                // The "Cancel" button at the bottom
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("CANCEL")
                    }
                }
            }
        }
    }
}

/**
 * A new, attractive composable for a single playlist row inside the dialog.
 */
@Composable
private fun DialogPlaylistItem(
    playlistWithSongs: PlaylistWithSongs,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(12.dp)
        ) {
            // A small placeholder image
            Image(
                painter = painterResource(id = R.drawable.ic_music_note_fallback),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Playlist name and song count
            Column {
                Text(
                    text = playlistWithSongs.playlist.name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${playlistWithSongs.songs.size} songs",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}