package com.sdu.composemusicplayer.viewmodel

import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.PlaySource
import kotlinx.coroutines.flow.Flow

@Suppress("TooManyFunctions")
interface IPlayerEnvironment {
    // 🎵 상태 관련
    fun getAllMusics(): Flow<List<Music>> // 전체 음악 목록

    fun observeQueue(): Flow<List<Music>> // 현재 큐

    fun getCurrentPlayedMusic(): Flow<Music> // 현재 재생 중인 음악

    fun getCurrentIndex(): Flow<Int> // 현재 큐 인덱스

    // ▶️ 재생 상태 관련
    fun isPlaying(): Flow<Boolean>

    fun isPaused(): Flow<Boolean>

    fun getCurrentDuration(): Flow<Long>

    fun isBottomMusicPlayerShowed(): Flow<Boolean>

    // ⏯️ 재생 제어
    suspend fun play(music: Music, playSource: PlaySource, playList: List<Music>? = null) // queue 업데이트 없이 단일 음악 재생

    suspend fun playAt(index: Int) // queue의 특정 인덱스부터 재생

    suspend fun pause()

    suspend fun resume()

    suspend fun previous()

    suspend fun next()

    // ⏱️ 위치 제어
    fun snapTo(
        duration: Long,
        fromUser: Boolean = true,
    )

    // 🧩 큐 및 설정 관련
    suspend fun updateQueue(queue: List<Music>)

    suspend fun updateMusicList(musicList: List<Music>)

    suspend fun refreshMusicList()

    suspend fun setShowBottomMusicPlayer(isShowed: Boolean)

    suspend fun resetIsPaused()
}
