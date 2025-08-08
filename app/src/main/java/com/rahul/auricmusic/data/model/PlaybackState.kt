// In data/model/PlaybackState.kt
package com.rahul.auricmusic.data.model

data class PlaybackState(
    val currentPlayingSong: Song? = null,
    val isPlaying: Boolean = false,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L
)