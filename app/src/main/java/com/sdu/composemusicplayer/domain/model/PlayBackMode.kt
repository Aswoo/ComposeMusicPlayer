package com.sdu.composemusicplayer.domain.model


enum class PlayBackMode {
    REPEAT_ALL, REPEAT_ONE, REPEAT_OFF;

    fun next() = when (this) {
        REPEAT_ALL -> REPEAT_ONE
        REPEAT_ONE -> REPEAT_OFF
        REPEAT_OFF -> REPEAT_ALL
    }
}