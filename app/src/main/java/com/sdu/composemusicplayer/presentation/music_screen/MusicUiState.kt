package com.sdu.composemusicplayer.presentation.music_screen

import com.sdu.composemusicplayer.data.roomdb.MusicEntity

data class MusicUiState(
    val musicList: List<MusicEntity> = emptyList(),
    val currentPlayedMusic: MusicEntity = MusicEntity.default,
    val currentDuration : Long = 0L,
    val isPlaying: Boolean = false,
    val isBottomPlayerShow : Boolean = false
) {
}