package com.sdu.composemusicplayer.viewmodel

import com.sdu.composemusicplayer.domain.model.Music

sealed interface PlayerEvent {
    data class Play(val music: Music) : PlayerEvent

    data class PlayPause(val isPlaying: Boolean) : PlayerEvent

    data class SetShowBottomPlayer(val isShow: Boolean) : PlayerEvent

    data class SnapTo(val duration: Long) : PlayerEvent

    data class UpdateMusicList(val musicList: List<Music>) : PlayerEvent

    object Previous : PlayerEvent

    object Next : PlayerEvent

    object RefreshMusicList : PlayerEvent

    object ResetIsPaused : PlayerEvent

    data class AddToQueue(val music: Music) : PlayerEvent

    object ClearQueue : PlayerEvent

    data class PlayPlaylist(val playlistId: String) : PlayerEvent
}
