package com.sdu.composemusicplayer.presentation.component.action

import android.content.Context
import com.sdu.composemusicplayer.domain.model.Music

fun interface MusicShareAction {
    fun share(
        context: Context,
        music: List<Music>,
    )
}
