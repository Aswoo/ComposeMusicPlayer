
package com.sdu.composemusicplayer.data.lyrics

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
