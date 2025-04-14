package com.sdu.composemusicplayer.data.music

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MusicRepository
    @Inject
    constructor(
        db: MusicDB,
    ) {
        private val musicDao = db.musicDao()

        fun getAllMusics(): Flow<List<MusicEntity>> = musicDao.getAllMusices()

        suspend fun insertMusic(music: MusicEntity) = musicDao.insert(music)

        suspend fun insertMusics(vararg music: MusicEntity) = musicDao.insert(*music)

        suspend fun deleteMusics(vararg music: MusicEntity) = musicDao.delete(*music)
    }
