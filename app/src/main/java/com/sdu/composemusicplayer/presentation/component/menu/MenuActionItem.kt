package com.sdu.composemusicplayer.presentation.component.menu

import android.content.Context
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.PlaylistRemove
import androidx.compose.material.icons.rounded.Share
import androidx.compose.ui.graphics.vector.ImageVector
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.presentation.component.action.MusicDeleteAction
import com.sdu.composemusicplayer.presentation.component.action.MusicShareAction

data class MenuActionItem(
    val icon: ImageVector,
    val title: String,
    val callback: () -> Unit
)


fun MutableList<MenuActionItem>.delete(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.Delete, "Delete", callback))

fun MutableList<MenuActionItem>.removeFromPlaylist(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.PlaylistRemove, "Remove from Playlist", callback))

fun MutableList<MenuActionItem>.share(callback: () -> Unit) =
    add(MenuActionItem(Icons.Rounded.Share, "Share", callback))

fun buildCommonMusicActions(
    music: Music,
    context: Context,
    shareAction: MusicShareAction,
    songDeleteAction: MusicDeleteAction,
): MutableList<MenuActionItem> {
    val musicList = listOf(music)
    val list = mutableListOf<MenuActionItem>().apply {
        share { shareAction.share(context, musicList) }
        delete { songDeleteAction.deleteMusic(musicList) }
    }
    return list
}

fun buildPlayListMusicActions(
    music: Music,
    context: Context,
    shareAction: MusicShareAction,
): MutableList<MenuActionItem> {
    val musicList = listOf(music)
    val list = mutableListOf<MenuActionItem>().apply {
        share { shareAction.share(context, musicList) }
    }
    return list
}