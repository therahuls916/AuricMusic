package com.rahul.auricmusic.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")

data class Song(
    @PrimaryKey val id: Long,
    val title: String,
    val artist: String,
    val songUri: String,
    val albumArtUri: String?
)