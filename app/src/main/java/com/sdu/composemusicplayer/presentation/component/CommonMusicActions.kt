package com.sdu.composemusicplayer.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import com.sdu.composemusicplayer.core.media.MediaRepository
import com.sdu.composemusicplayer.presentation.component.action.MusicDeleteAction
import com.sdu.composemusicplayer.presentation.component.action.MusicShareAction
import com.sdu.composemusicplayer.presentation.component.action.MusicSharer
import com.sdu.composemusicplayer.presentation.component.action.rememberSongDeleter

data class CommonSongsActions(
    val shareAction: MusicShareAction,
    val deleteAction: MusicDeleteAction,
)

val LocalCommonMusicAction = staticCompositionLocalOf<CommonSongsActions> { throw IllegalArgumentException("not implemented") }

@Composable
fun rememberCommonMusicActions(mediaRepository: MediaRepository): CommonSongsActions {
    val context = LocalContext.current
    val shareAction = MusicSharer
    val deleteAction = rememberSongDeleter(mediaRepository = mediaRepository)

    return remember {
        CommonSongsActions(
            shareAction,
            deleteAction,
        )
    }
}
