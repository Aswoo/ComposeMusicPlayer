package com.sdu.composemusicplayer.domain.model

enum class SortOption(val displayName: String) {
    TITLE("제목"),
    ARTIST("아티스트"),
    ALBUM("앨범")
}

enum class SortDirection {
    ASCENDING, DESCENDING;

    fun toggle() = if (this == ASCENDING) DESCENDING else ASCENDING
}

data class SortState(
    val option: SortOption = SortOption.TITLE,
    val direction: SortDirection = SortDirection.ASCENDING
)