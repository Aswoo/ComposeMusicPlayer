package com.sdu.composemusicplayer.viewmodel

import com.sdu.composemusicplayer.data.roomdb.MusicEntity

sealed interface PlayerEvent {
    data class Play(val music: MusicEntity) : PlayerEvent
    data class PlayPause(val isPlaying: Boolean) : PlayerEvent
    data class SetShowBottomPlayer(val isShow: Boolean) : PlayerEvent
    data class SnapTo(val duration: Long) : PlayerEvent
    data class UpdateMusicList(val musicList: List<MusicEntity>) : PlayerEvent
    object Previous : PlayerEvent
    object Next : PlayerEvent
    object RefreshMusicList : PlayerEvent
    object ResetIsPaused : PlayerEvent
}
