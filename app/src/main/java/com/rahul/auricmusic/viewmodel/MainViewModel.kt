// In viewmodel/MainViewModel.kt
package com.rahul.auricmusic.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.rahul.auricmusic.data.model.PlaybackState
import com.rahul.auricmusic.data.model.Song
import com.rahul.auricmusic.player.MediaControllerManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _playbackState = MutableStateFlow(PlaybackState())
    val playbackState = _playbackState.asStateFlow()

    private var allSongs: List<Song> = emptyList()

    // A flag to ensure we only set the playlist on the player once.
    private var isPlaylistSet = false

    private var playerListener: Player.Listener? = null

    init {
        viewModelScope.launch {
            MediaControllerManager.mediaController.collectLatest { mediaController ->
                if (mediaController == null) return@collectLatest
                playerListener = object : Player.Listener {
                    override fun onEvents(player: Player, events: Player.Events) {
                        updatePlaybackState(player)
                    }
                }.also {
                    mediaController.addListener(it)
                }
                while (true) {
                    updatePlaybackState(mediaController)
                    delay(1000)
                }
            }
        }
    }

    private fun updatePlaybackState(player: Player?) {
        val currentMediaItem = player?.currentMediaItem ?: return
        val currentSong = allSongs.find { it.id.toString() == currentMediaItem.mediaId }

        _playbackState.value = _playbackState.value.copy(
            isPlaying = player.isPlaying,
            currentPosition = player.currentPosition.coerceAtLeast(0L),
            totalDuration = player.duration.coerceAtLeast(0L),
            currentPlayingSong = currentSong
        )
    }

    fun onSongListLoaded(songs: List<Song>) {
        // If the playlist is already set or the incoming list is empty, do nothing.
        if (isPlaylistSet || songs.isEmpty()) {
            return
        }


        allSongs = songs
        val mediaItems = songs.map { song ->
            MediaItem.Builder()
                .setUri(song.songUri)
                .setMediaId(song.id.toString())
                .build()
        }
        MediaControllerManager.mediaController.value?.setMediaItems(mediaItems)
        MediaControllerManager.mediaController.value?.prepare()

        // Mark the playlist as set so this block doesn't run again.
        isPlaylistSet = true

    }

    fun playSong(song: Song) {
        _playbackState.value = _playbackState.value.copy(currentPlayingSong = song)
        val mediaController = MediaControllerManager.mediaController.value ?: return
        val songIndex = allSongs.indexOf(song)
        if (songIndex != -1) {
            mediaController.seekTo(songIndex, 0)
            mediaController.play()
        }
    }

    fun resume() = MediaControllerManager.mediaController.value?.play()
    fun pause() = MediaControllerManager.mediaController.value?.pause()
    fun seekTo(position: Long) = MediaControllerManager.mediaController.value?.seekTo(position)
    fun skipToNext() = MediaControllerManager.mediaController.value?.seekToNextMediaItem()
    fun skipToPrevious() = MediaControllerManager.mediaController.value?.seekToPreviousMediaItem()

    companion object {
        const val SEEK_INTERVAL_MS = 10000L // 10 seconds
    }

    fun rewind() {
        val mediaController = MediaControllerManager.mediaController.value ?: return
        val newPosition = (mediaController.currentPosition - SEEK_INTERVAL_MS).coerceAtLeast(0L)
        mediaController.seekTo(newPosition)
    }

    fun fastForward() {
        val mediaController = MediaControllerManager.mediaController.value ?: return
        val newPosition = (mediaController.currentPosition + SEEK_INTERVAL_MS).coerceAtMost(mediaController.duration)
        mediaController.seekTo(newPosition)
    }

    override fun onCleared() {
        super.onCleared()
        playerListener?.let {
            MediaControllerManager.mediaController.value?.removeListener(it)
        }
    }
}