package com.sdu.composemusicplayer.presentation.music_screen

import com.sdu.composemusicplayer.data.roomdb.MusicEntity

sealed interface PlayerEvent {
    data class Play(val music : MusicEntity) : PlayerEvent
    data class PlayPause(val isPlaying : Boolean) : PlayerEvent
    data class SetShowBottomPlayer(val isShow : Boolean) : PlayerEvent
    object RefreshMusicList: PlayerEvent
}