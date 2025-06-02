package com.sdu.composemusicplayer.viewmodel

import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.SortState

data class MusicUiState(
    val musicList: List<Music> = emptyList(),
    val queue: List<Music> = emptyList(),
    val currentQueueIndex: Int = 0,
    val currentPlayedMusic: Music = Music.default,
    val currentDuration: Long = 0L,
    val isPlaying: Boolean = false,
    val isBottomPlayerShow: Boolean = false,
    val isPaused: Boolean = false,
    val sortState: SortState = SortState(),
)
