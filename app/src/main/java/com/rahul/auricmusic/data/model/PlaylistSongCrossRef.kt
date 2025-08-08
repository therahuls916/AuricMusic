// In data/model/PlaylistSongCrossRef.kt
package com.rahul.auricmusic.data.model

import androidx.room.Entity

@Entity(tableName = "playlist_song_cross_ref", primaryKeys = ["playlistId", "songId"])
data class PlaylistSongCrossRef(
    val playlistId: Long,
    val songId: Long
)