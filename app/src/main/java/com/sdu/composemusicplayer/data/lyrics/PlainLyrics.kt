package com.sdu.composemusicplayer.data.lyrics


data class PlainLyrics (
    val lines: List<String>
) {
    companion object {
        fun fromString(s: String): PlainLyrics = PlainLyrics(s.split('\n'))
    }

    fun constructStringForSharing(): String {
        return lines.joinToString(separator = "\n") { it }
    }
}