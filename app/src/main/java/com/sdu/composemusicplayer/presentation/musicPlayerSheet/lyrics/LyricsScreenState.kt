package com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics

import com.sdu.composemusicplayer.core.model.lyrics.LyricsFetchSource
import com.sdu.composemusicplayer.core.model.lyrics.PlainLyrics
import com.sdu.composemusicplayer.core.model.lyrics.SynchronizedLyrics

sealed interface LyricsScreenState {
    data object Loading : LyricsScreenState

    data object NotPlaying : LyricsScreenState

    data object SearchingLyrics : LyricsScreenState

    data class TextLyrics(
        val plainLyrics: PlainLyrics,
        val lyricsSource: LyricsFetchSource,
    ) : LyricsScreenState

    data class SyncedLyrics(
        val syncedLyrics: SynchronizedLyrics,
        val lyricsSource: LyricsFetchSource,
    ) : LyricsScreenState

    data class NoLyrics(val reason: NoLyricsReason) : LyricsScreenState
}

enum class NoLyricsReason {
    NETWORK_ERROR,
    NOT_FOUND,
}
