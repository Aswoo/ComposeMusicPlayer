@file:Suppress("MaxLineLength")
package com.sdu.composemusicplayer.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sdu.composemusicplayer.core.database.dao.LyricsDao
import com.sdu.composemusicplayer.core.database.dao.MusicDao
import com.sdu.composemusicplayer.core.database.dao.PlaylistDao
import com.sdu.composemusicplayer.core.database.dao.QueueDao
import com.sdu.composemusicplayer.core.database.entity.LyricsEntity
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import com.sdu.composemusicplayer.core.database.entity.PlaylistEntity
import com.sdu.composemusicplayer.core.database.entity.PlaylistsMusicEntity
import com.sdu.composemusicplayer.core.database.entity.QueueEntity

@Database(
    entities = [MusicEntity::class, LyricsEntity::class, QueueEntity::class, PlaylistEntity::class, PlaylistsMusicEntity::class],
    version = 4,
)
abstract class MusicDB : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    abstract fun lyricsDao(): LyricsDao

    abstract fun queueDao(): QueueDao

    abstract fun playListDao(): PlaylistDao

    companion object {
        @Volatile // For Singleton
        private var instance: MusicDB? = null

        fun getInstance(context: Context): MusicDB {
            return instance ?: synchronized(this) {
                instance ?: buildDatabase(context).also { instance = it }
            }
        }

        private fun buildDatabase(context: Context): MusicDB {
            return Room
                .databaseBuilder(
                    context,
                    MusicDB::class.java,
                    "music_db_0903",
                ).allowMainThreadQueries()
                .fallbackToDestructiveMigration() // for Test
                .build()
        }
    }
}
