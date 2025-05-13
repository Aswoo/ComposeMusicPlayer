
package com.sdu.composemusicplayer.core.database.model

import com.sdu.composemusicplayer.core.model.lyrics.LyricsFetchSource
import com.sdu.composemusicplayer.core.model.lyrics.PlainLyrics
import com.sdu.composemusicplayer.core.model.lyrics.SynchronizedLyrics

sealed interface LyricsResult {

    data object NotFound: LyricsResult

    data object NetworkError: LyricsResult

    data class FoundPlainLyrics(
        val plainLyrics: PlainLyrics,
        val lyricsSource: LyricsFetchSource
    ): LyricsResult

    data class FoundSyncedLyrics(
        val syncedLyrics: SynchronizedLyrics,
        val lyricsSource: LyricsFetchSource
    ): LyricsResult
}
