package com.sdu.composemusicplayer.data.music

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.sdu.composemusicplayer.data.lyrics.LyricsDao
import com.sdu.composemusicplayer.data.lyrics.LyricsEntity
import com.sdu.composemusicplayer.data.queue.QueueDao
import com.sdu.composemusicplayer.data.queue.QueueEntity

@Database(entities = [MusicEntity::class, LyricsEntity::class, QueueEntity::class], version = 3)
abstract class MusicDB : RoomDatabase() {
    abstract fun musicDao(): MusicDao
    abstract fun lyricsDao(): LyricsDao
    abstract fun queueDao(): QueueDao

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
