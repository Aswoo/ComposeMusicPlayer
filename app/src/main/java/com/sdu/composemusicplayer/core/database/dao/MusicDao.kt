package com.sdu.composemusicplayer.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {
    @Query("SELECT * FROM MusicEntity")
    fun getAllMusices(): Flow<List<MusicEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(music: MusicEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(music: List<MusicEntity>)

    @Delete
    suspend fun delete(music: List<MusicEntity>)
}
