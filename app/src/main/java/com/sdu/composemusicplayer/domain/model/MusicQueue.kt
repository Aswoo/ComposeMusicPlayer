package com.sdu.composemusicplayer.domain.model

/**
 * Represents a music queue with a list of songs and current playing index.
 */
data class MusicQueue(
    val items: MutableList<QueueItem> = mutableListOf(),
    var currentIndex: Int = 0
) {
    val currentItem: QueueItem?
        get() = items.getOrNull(currentIndex)

    fun hasNext(): Boolean = currentIndex + 1 < items.size
    fun hasPrevious(): Boolean = currentIndex - 1 >= 0

    fun skipToNext(): Boolean {
        if (hasNext()) {
            currentIndex++
            return true
        }
        return false
    }

    fun skipToPrevious(): Boolean {
        if (hasPrevious()) {
            currentIndex--
            return true
        }
        return false
    }

    fun insertNext(music: Music) {
        val insertIndex = currentIndex + 1
        items.add(insertIndex, QueueItem(music, originalIndex = insertIndex))
    }

    fun removeAt(index: Int): Boolean {
        if (index in items.indices) {
            items.removeAt(index)
            if (index < currentIndex) currentIndex--
            return true
        }
        return false
    }

    companion object {
        val EMPTY = MusicQueue(mutableListOf(), 0)
    }
}

/**
 * A single song inside the queue
 *
 * @param music The song to be played
 * @param originalIndex The index in the original non-shuffled queue. Can be used as id in LazyLists
 */
data class QueueItem(
    val music: Music,
    val originalIndex: Int
)
