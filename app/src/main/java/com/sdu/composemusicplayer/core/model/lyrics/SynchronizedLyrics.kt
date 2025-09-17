package com.sdu.composemusicplayer.core.model.lyrics

/**
 * Synced Lyrics containing a list of [SyncedLyricsSegment]s
 */
data class SynchronizedLyrics(
    val segments: List<SyncedLyricsSegment>,
) {
    fun constructStringForSharing(): String {
        return segments.joinToString(separator = "\n") { it.text }
    }

    companion object {
        private const val MILLIS_IN_SECOND = 1000
        private const val SECONDS_IN_MINUTE = 60
        private const val CENTISECONDS_TO_MILLISECONDS = 10

        fun fromString(text: String?): SynchronizedLyrics? {
            if (text.isNullOrBlank()) return null

            val segments = text.lines()
                .mapNotNull { parseLine(it) }

            if (segments.isEmpty()) return null
            return SynchronizedLyrics(segments)
        }

        private fun parseLine(line: String): SyncedLyricsSegment? {
            if (!line.startsWith("[")) return null

            val timeInfoLastIndex = line.indexOfFirst { it == ']' }
            if (timeInfoLastIndex == -1) return null

            val timeInfo = line.substring(1, timeInfoLastIndex)
            val timeInfoArray = timeInfo.split(":")

            val minutes = timeInfoArray.getOrNull(0)?.toIntOrNull() ?: return null

            val secondsArray = timeInfoArray.getOrNull(1)?.split(".") ?: return null
            val seconds = secondsArray.getOrNull(0)?.toIntOrNull() ?: return null
            val millis = secondsArray.getOrNull(1)?.toIntOrNull()?.times(CENTISECONDS_TO_MILLISECONDS) ?: return null

            val text = line.substring(timeInfoLastIndex + 1).trim()

            return SyncedLyricsSegment(
                text,
                minutes * SECONDS_IN_MINUTE * MILLIS_IN_SECOND + seconds * MILLIS_IN_SECOND + millis,
            )
        }
    }
}

/**
 * Represents a single line of a synced lyrics text
 *
 * @param text The line of the lyrics
 * @param durationMillis Duration in milliseconds since the start of the song
 */
data class SyncedLyricsSegment(
    val text: String,
    val durationMillis: Int,
)