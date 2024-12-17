package com.sdu.composemusicplayer.viewmodel

import com.sdu.composemusicplayer.data.roomdb.MusicEntity
import kotlinx.coroutines.flow.Flow

interface IPlayerEnvironment {
    // 기존 메소드들
    fun getAllMusics(): Flow<List<MusicEntity>>
    fun getCurrentPlayedMusic(): Flow<MusicEntity>
    fun isPlaying(): Flow<Boolean>
    fun isBottomMusicPlayerShowed(): Flow<Boolean>
    fun getCurrentDuration(): Flow<Long>
    fun isPaused(): Flow<Boolean>

    // 기존 suspend 메소드들
    suspend fun play(music: MusicEntity)
    suspend fun pause()
    suspend fun resume()
    suspend fun previous()
    suspend fun next()
    fun snapTo(duration: Long, fromUser: Boolean = true)
    suspend fun setShowBottomMusicPlayer(isShowed: Boolean)
    suspend fun updateMusicList(musicList: List<MusicEntity>)
    suspend fun refreshMusicList()
    suspend fun resetIsPaused()
}
