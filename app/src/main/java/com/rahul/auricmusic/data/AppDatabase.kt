// In data/AppDatabase.kt
package com.rahul.auricmusic.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rahul.auricmusic.data.model.FavoriteSong
import com.rahul.auricmusic.data.model.Playlist
import com.rahul.auricmusic.data.model.PlaylistSongCrossRef
import com.rahul.auricmusic.data.model.Song

/**
 * The main Room database for the application.
 * It includes all the tables (entities) needed for the app's features.
 * Version is incremented to 2 because we added new tables for the playlist feature.
 */
@Database(
    entities = [
        FavoriteSong::class,
        Playlist::class,
        PlaylistSongCrossRef::class,
        Song::class
    ],
    version = 2, // Version must be incremented when the schema changes.
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun favoriteSongDao(): FavoriteSongDao
    abstract fun playlistDao(): PlaylistDao
    abstract fun songDao(): SongDao

    companion object {
        // @Volatile ensures that writes to this field are immediately visible to other threads.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if it's not null, otherwise create a new one.
            // The synchronized block ensures that only one thread can create the instance at a time.
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "auric_music_database"
                )
                    // This is a migration strategy. For development, it's okay to just
                    // destroy and recreate the database if the schema changes.
                    // For a production app, you would need to provide a proper migration path.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return the newly created instance
                instance
            }
        }
    }
}