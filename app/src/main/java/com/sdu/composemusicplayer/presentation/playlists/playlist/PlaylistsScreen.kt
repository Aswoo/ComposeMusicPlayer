package com.sdu.composemusicplayer.presentation.playlists.playlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import com.sdu.composemusicplayer.domain.playback.PlaylistPlaybackActions
import com.sdu.composemusicplayer.presentation.playlists.rememberCreatePlaylistDialog


@Composable
fun PlaylistsScreen(
    modifier: Modifier,
    onNavigateToPlaylist: (Int) -> Unit,
    playlistsViewModel: PlaylistsViewModel = hiltViewModel()
) {

    val state by playlistsViewModel.state.collectAsState()

    PlaylistsScreen(
        modifier = modifier,
        state = state,
        onNavigateToPlaylist,
        playlistsViewModel::onDelete,
        playlistsViewModel::onRename,
        playlistsViewModel,
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    modifier: Modifier,
    state: PlaylistsScreenState,
    onPlaylistClicked: (Int) -> Unit,
    onDeletePlaylist: (Int) -> Unit,
    onRenamePlaylist: (Int, String) -> Unit,
    playlistPlaybackActions: PlaylistPlaybackActions,
) {

    val createPlaylistsDialog = rememberCreatePlaylistDialog()

    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = modifier,
        topBar = {

            TopAppBar(
                title = { Text(text = "Playlists", fontWeight = FontWeight.SemiBold) },
                actions = {
                    IconButton(onClick = { createPlaylistsDialog.launch() }) {
                        Icon(imageVector = Icons.Rounded.Add, contentDescription = null)
                    }
                },
                scrollBehavior = topBarScrollBehavior
            )

        }
    ) { paddingValues ->

        if (state is PlaylistsScreenState.Loading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection)
                    .padding(top = paddingValues.calculateTopPadding()),

                ) {

                item {
                    Divider(Modifier.fillMaxWidth())
                }

                val list = (state as PlaylistsScreenState.Success).playlists

                items(list) {

                    var currentRenameId by remember { mutableStateOf<Int?>(null) }
                    BackHandler(currentRenameId != null) {
                        currentRenameId = null
                    }


                    val deletePlaylistDialog = rememberDeletePlaylistDialog(playlistName = it.name)
                    { onDeletePlaylist(it.id) }

                    PlaylistRow(
                        Modifier
                            .fillMaxWidth()
                            .clickable { onPlaylistClicked(it.id) },
                        it,
                        playlistPlaybackActions = playlistPlaybackActions,
                        inRenameMode = currentRenameId == it.id,
                        onEnableRenameMode = { currentRenameId = it.id },
                        { name -> onRenamePlaylist(it.id, name); currentRenameId = null },
                        { deletePlaylistDialog.launch() }
                    )
                    if (it != list.last()) {
                        Divider(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = (12 + 36 + 8).dp)
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.navigationBarsPadding())
                }

            }

    }


}

@Composable
fun PlaylistRow(
    modifier: Modifier,
    playlistInfo: PlaylistInfo,
    playlistPlaybackActions: PlaylistPlaybackActions,
    inRenameMode: Boolean,
    onEnableRenameMode: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        PlaylistInfoRow(
            modifier = Modifier.weight(1f),
            playlistInfo = playlistInfo,
            inRenameMode, onRename, onEnableRenameMode
        )
//        OverflowMenu(
//            actionItems = buildSinglePlaylistActions(
//                playlistInfo.id,
//                playlistPlaybackActions,
//                onEnableRenameMode, onDelete
//            ), showIcons = false
//        )
        Spacer(modifier = Modifier.width(8.dp))
    }

}