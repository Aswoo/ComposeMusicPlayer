package com.sdu.composemusicplayer.core.model.playlist

import com.sdu.composemusicplayer.domain.model.Music

class MusicUriMapper(
    val musicList: List<Music>,
) {
    /**
     * Map of music Uri to their Uris
     */
    private val musicMap: Map<String, Music> =
        kotlin.run {
            val map = mutableMapOf<String, Music>()
            musicList.forEach { music ->
                map[music.audioPath] = music
            }
            map
        }

    fun getMusicByUri(uri: String): Music? = musicMap[uri]

    fun getMusicByUris(uris: List<String>): List<Music> =
        kotlin.run {
            val result = mutableListOf<Music>()
            uris.forEach {
                val music = musicMap[it] ?: return@forEach
                result.add(music)
            }
            result
        }
}
