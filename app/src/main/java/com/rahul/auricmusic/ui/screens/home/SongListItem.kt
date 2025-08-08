// In ui/screens/home/SongListItem.kt
package com.rahul.auricmusic.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.rahul.auricmusic.R
import com.rahul.auricmusic.data.model.Song

@Composable
fun SongListItem(
    song: Song,
    isFavorite: Boolean,
    onFavoriteClick: (Boolean) -> Unit,
    onAddToPlaylistClick: () -> Unit,
    // This is a nullable lambda. If it's null, the button won't be shown.
    onRemoveFromPlaylistClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp, top = 12.dp, bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = rememberAsyncImagePainter(
                    model = song.albumArtUri,
                    error = painterResource(id = R.drawable.ic_music_note_fallback)
                ),
                contentDescription = "Album Art",
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = song.title,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Favorite Icon Button
            IconButton(onClick = { onFavoriteClick(isFavorite) }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Favorite",
                    tint = if (isFavorite) MaterialTheme.colorScheme.primary else Color.Gray
                )
            }

            // The Delete icon is ONLY shown if the onRemoveFromPlaylistClick lambda is provided.
            if (onRemoveFromPlaylistClick != null) {
                IconButton(onClick = onRemoveFromPlaylistClick) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove from Playlist",
                        tint = Color.Gray
                    )
                }
            }

            // The "More" button for adding to another playlist
            IconButton(onClick = onAddToPlaylistClick) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = "Add to another playlist",
                    tint = Color.Gray
                )
            }
        }
    }
}