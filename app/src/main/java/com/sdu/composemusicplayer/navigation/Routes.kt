package com.sdu.composemusicplayer.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LibraryMusic
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.rounded.LibraryMusic
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.ui.graphics.vector.ImageVector

const val MAIN_NAVIGATION_GRAPH = "main_graph"
const val PLAYLISTS_NAVIGATION_GRAPH = "playlist_graph"
const val SETTINGS_NAVIGATION_GRAPH = "setting_graph"

enum class Routes(
    val iconSelected: ImageVector,
    val iconNotSelected: ImageVector,
    val title: String,
    val route: String,
) {
    Main(
        Icons.Rounded.MusicNote,
        Icons.Outlined.MusicNote,
        "Main",
        MAIN_NAVIGATION_GRAPH,
    ),
    PLAYLISTS(
        Icons.Rounded.LibraryMusic,
        Icons.Outlined.LibraryMusic,
        "Playlists",
        PLAYLISTS_NAVIGATION_GRAPH,
    ),
    SETTINGS(
        Icons.Rounded.Settings,
        Icons.Outlined.Settings,
        "Settings",
        SETTINGS_NAVIGATION_GRAPH,
    ),
}
