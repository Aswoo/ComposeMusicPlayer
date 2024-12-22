package com.sdu.composemusicplayer.data.roomdb

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class MusicRepository @Inject constructor(
    db: MusicDB,
) {
    private val musicDao = db.musicDao()

    fun getAllMusics(): Flow<List<MusicEntity>> = musicDao.getAllMusices()

    suspend fun insertMusic(music: MusicEntity) = musicDao.insert(music)

    suspend fun insertMusics(vararg music: MusicEntity) = musicDao.insert(*music)

    suspend fun deleteMusics(vararg music: MusicEntity) = musicDao.delete(*music)
}
