// In player/NowPlayingScreen.kt

package com.rahul.auricmusic.player

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.rahul.auricmusic.R
import com.rahul.auricmusic.ui.theme.Goldenrod
import com.rahul.auricmusic.viewmodel.HomeViewModel
import com.rahul.auricmusic.viewmodel.MainViewModel
import java.util.concurrent.TimeUnit
import androidx.compose.animation.core.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.runtime.*
import androidx.compose.ui.input.pointer.pointerInput


@Composable
fun NowPlayingScreen(
    mainViewModel: MainViewModel,
    homeViewModel: HomeViewModel = viewModel(),
    onBackClick: () -> Unit
) {
    val playbackState by mainViewModel.playbackState.collectAsState()
    val currentSong = playbackState.currentPlayingSong

    val infiniteTransition = rememberInfiniteTransition(label = "glow_transition")
    var totalHorizontalDrag by remember { mutableFloatStateOf(0f) }

    // 1. Create a brush that contains all our rainbow colors
    val rainbowBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFFFF0000), // Red
                Color(0xFFFF7F00), // Orange
                Color(0xFFFFFF00), // Yellow
                Color(0xFF00FF00), // Green
                Color(0xFF0000FF), // Blue
                Color(0xFF4B0082), // Indigo
                Color(0xFF9400D3), // Violet
                Color(0xFFFF0000)  // Loop back to Red
            )
        )
    }

    // 2. Animate a rotation angle for the gradient brush
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart // Keep spinning in the same direction
        ),
        label = "rainbow_angle"
    )

    // 3. Animate the border width to create a pulse
    val animatedWidth by infiniteTransition.animateFloat(
        initialValue = 2f, // Start at 2dp width
        targetValue = 6f,  // Pulse out to 6dp width
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_width"
    )


    if (currentSong == null) {
        Box(modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(onClick = onBackClick, modifier = Modifier.align(Alignment.Start)) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
                Spacer(modifier = Modifier.weight(1f))
                Text("Loading Song...")
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        return
    }

    // Observe the favorite status for the current song.
    val isFavorite by homeViewModel.isFavorite(currentSong.id).collectAsState(initial = false)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", modifier = Modifier.size(28.dp), tint = Color.White)
            }
            // THIS IS THE FULLY FUNCTIONAL FAVORITE BUTTON
            IconButton(onClick = {
                homeViewModel.toggleFavorite(currentSong.id, isFavorite)
            }) {
                Icon(
                    imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorite",
                    modifier = Modifier.size(28.dp),
                    tint = if (isFavorite) Goldenrod else Color.White
                )
            }
        }

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .aspectRatio(1f)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragStart = {
                            // Reset the drag amount when a new gesture starts
                            totalHorizontalDrag = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            // Consume the event and accumulate the drag distance
                            change.consume()
                            totalHorizontalDrag += dragAmount
                        },
                        onDragEnd = {
                            // Define a threshold for what counts as a swipe (e.g., 50dp)
                            val swipeThreshold = 50.dp.toPx()

                            if (totalHorizontalDrag < -swipeThreshold) {
                                // Swiped from Right to Left (negative value) -> NEXT song
                                mainViewModel.skipToNext()
                            } else if (totalHorizontalDrag > swipeThreshold) {
                                // Swiped from Left to Right (positive value) -> PREVIOUS song
                                mainViewModel.skipToPrevious()
                            }
                        }
                    )
                }
                // 1. Apply the drawing modifier to the OUTER BOX.
                .drawBehind {
                    drawRoundRect(
                        brush = rainbowBrush, // Or use a solid color for the simple pulse
                        cornerRadius = CornerRadius(20.dp.toPx()),
                        style = Stroke(width = animatedWidth.dp.toPx())
                    )
                }
        ) {
            // The Image composable now sits inside the Box.
            Image(
                painter = rememberAsyncImagePainter(
                    model = currentSong.albumArtUri,
                    error = painterResource(id = R.drawable.ic_music_note_fallback)
                ),
                contentDescription = "Album Art",
                // 2. The Image is slightly smaller than the Box, creating space for the glow.
                modifier = Modifier
                    .matchParentSize() // Make the image fill the Box...
                    .padding(2.dp, 2.dp)     // ... but with padding on all sides.
                    .shadow(elevation = 16.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp)), // Clip the image with slightly smaller corners.
                contentScale = ContentScale.Crop
            )
        }

        // Song Title and Artist
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = currentSong.title, fontSize = 24.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = currentSong.artist, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Seek Bar with Timestamps
        Column(modifier = Modifier.fillMaxWidth()) {
            Slider(
                value = playbackState.currentPosition.toFloat(),
                onValueChange = { newPosition -> mainViewModel.seekTo(newPosition.toLong()) },
                valueRange = 0f..playbackState.totalDuration.toFloat().coerceAtLeast(1f),
                colors = SliderDefaults.colors(thumbColor = Color.White, activeTrackColor = Color.White, inactiveTrackColor = Color.Gray.copy(alpha = 0.5f))
            )
            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = formatDuration(playbackState.currentPosition), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = formatDuration(playbackState.totalDuration), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        // Playback Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { mainViewModel.skipToPrevious() }) {
                Icon(Icons.Filled.SkipPrevious, contentDescription = "Previous", modifier = Modifier.size(48.dp), tint = Color.White)
            }
            IconButton(onClick = {mainViewModel.rewind()}) {
                Icon(Icons.Filled.FastRewind, contentDescription = "Rewind", modifier = Modifier.size(48.dp), tint = Color.White)
            }
            IconButton(
                onClick = { if (playbackState.isPlaying) mainViewModel.pause() else mainViewModel.resume() },
                modifier = Modifier.size(72.dp).background(Goldenrod, CircleShape)
            ) {
                Icon(
                    imageVector = if (playbackState.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(48.dp),
                    tint = Color.Black
                )
            }
            IconButton(onClick = { mainViewModel.fastForward() }) {
                Icon(Icons.Filled.FastForward, contentDescription = "Fast Forward", modifier = Modifier.size(48.dp), tint = Color.White)
            }
            IconButton(onClick = { mainViewModel.skipToNext() }) {
                Icon(Icons.Filled.SkipNext, contentDescription = "Next", modifier = Modifier.size(48.dp), tint = Color.White)
            }
        }
    }
}

private fun formatDuration(millis: Long): String {
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    return String.format("%02d:%02d", minutes, seconds)
}