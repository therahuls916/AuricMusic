// In player/AuricMediaService.kt

package com.rahul.auricmusic.player

import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession

private class AuricMediaCallback : MediaLibraryService.MediaLibrarySession.Callback {
    // This class is intentionally empty for now.
}

class AuricMediaService : MediaLibraryService() {

    private var mediaSession: MediaLibrarySession? = null

    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaLibrarySession.Builder(this, player, AuricMediaCallback()).build()
    }


    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaSession
    }


    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }
}