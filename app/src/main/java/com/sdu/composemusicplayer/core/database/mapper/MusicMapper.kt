package com.sdu.composemusicplayer.core.database.mapper

import com.sdu.composemusicplayer.core.database.entity.MusicEntity
import com.sdu.composemusicplayer.domain.model.Music

fun MusicEntity.toDomain(): Music {
    return Music(
        audioId = audioId,
        title = title,
        artist = artist,
        duration = duration,
        albumPath = albumPath,
        audioPath = audioPath,
    )
}

fun Music.toEntity(): MusicEntity {
    return MusicEntity(
        audioId = audioId,
        title = title,
        artist = artist,
        duration = duration,
        albumPath = albumPath,
        audioPath = audioPath,
    )
}
