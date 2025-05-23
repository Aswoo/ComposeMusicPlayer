package com.sdu.composemusicplayer.data.music

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM MusicEntity")
    fun getAllMusices(): Flow<List<MusicEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(vararg music: MusicEntity)

    @Delete
    suspend fun delete(vararg music: MusicEntity)
}
