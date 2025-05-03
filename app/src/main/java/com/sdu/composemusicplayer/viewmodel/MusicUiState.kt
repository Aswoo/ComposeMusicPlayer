package com.sdu.composemusicplayer.viewmodel

import com.sdu.composemusicplayer.data.music.MusicEntity

data class MusicUiState(
    val musicList: List<MusicEntity> = emptyList(),  // 전체 음악
    val queue: List<MusicEntity> = emptyList(),      // 재생 순서
    val currentQueueIndex: Int = 0,                  // queue 내 현재 곡 위치
    val currentPlayedMusic: MusicEntity = MusicEntity.default,
    val currentDuration: Long = 0L,
    val isPlaying: Boolean = false,
    val isBottomPlayerShow: Boolean = false,
    val isPaused: Boolean = false,
)
