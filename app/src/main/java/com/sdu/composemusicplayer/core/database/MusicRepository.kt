package com.sdu.composemusicplayer.core.database

import android.content.Context
import android.net.Uri
import com.sdu.composemusicplayer.core.database.dao.MusicDao
import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class MusicRepository
    @Inject
    constructor(
        private val musicDao: MusicDao,
    ) {
        fun getAllMusics(): Flow<List<MusicEntity>> = musicDao.getAllMusices()

        suspend fun insertMusic(music: MusicEntity) = musicDao.insert(music)

        suspend fun insertMusics(vararg music: MusicEntity) = musicDao.insert(*music)

        suspend fun deleteMusics(
            vararg music: MusicEntity,
            context: Context,
        ) {
            music.forEach { music ->
                val deletedFromDevice =
                    context.contentResolver.delete(
                        Uri.parse(music.audioPath),
                        null,
                        null,
                    ) > 0

                if (!deletedFromDevice) {
                    throw IllegalStateException("기기에서 파일 삭제 실패: ${music.audioPath}")
                }
            }

            musicDao.delete(*music)
        }
    }
