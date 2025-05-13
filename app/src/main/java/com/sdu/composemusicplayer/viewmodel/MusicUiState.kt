package com.sdu.composemusicplayer.viewmodel

import com.sdu.composemusicplayer.domain.model.Music


data class MusicUiState(
    val musicList: List<Music> = emptyList(),  // 전체 음악
    val queue: List<Music> = emptyList(),      // 재생 순서
    val currentQueueIndex: Int = 0,                  // queue 내 현재 곡 위치
    val currentPlayedMusic: Music = Music.default,
    val currentDuration: Long = 0L,
    val isPlaying: Boolean = false,
    val isBottomPlayerShow: Boolean = false,
    val isPaused: Boolean = false,
)
