@file:OptIn(ExperimentalFoundationApi::class)

package com.sdu.composemusicplayer.presentation.playlists.playlist

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import com.sdu.composemusicplayer.domain.playback.PlaylistPlaybackActions
import com.sdu.composemusicplayer.presentation.component.dialog.rememberCreatePlaylistDialog
import com.sdu.composemusicplayer.presentation.playlists.playlistdetail.rememberDeletePlaylistDialog
import kotlinx.coroutines.launch


@Composable
fun PlaylistsScreen(
    modifier: Modifier,
    onNavigateToPlaylist: (Int) -> Unit,
    playlistsViewModel: PlaylistsViewModel = hiltViewModel(),
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

    val coroutineScope = rememberCoroutineScope() // 이걸 items 밖에 선언

    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedPlaylist by remember { mutableStateOf<PlaylistInfo?>(null) }

    // 이름 변경 다이얼로그 상태
    var renameMode by remember { mutableStateOf(false) }
    // 삭제 다이얼로그
    val deletePlaylistDialog = rememberDeletePlaylistDialog(
        playlistName = selectedPlaylist?.name ?: "",
    ) {
        selectedPlaylist?.id?.let { onDeletePlaylist(it) }
        selectedPlaylist = null
    }

    var showSheet by remember { mutableStateOf(false) }

    // ModalBottomSheet은 Scaffold 밖에 두는 게 일반적
    // sheetState와 onDismissRequest를 전달해야 함
    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState,
            dragHandle = { // Optional: 드래그 핸들 UI
                Box(
                    Modifier
                        .padding(vertical = 8.dp)
                        .size(width = 40.dp, height = 4.dp)
                        .background(MaterialTheme.colorScheme.onSurfaceVariant, shape = RoundedCornerShape(2.dp)),
                )
            },
        ) {
            // 시트 내용
            PlaylistBottomSheet(
                playlistName = selectedPlaylist?.name ?: "이름없는 플레이리스트",
                onRenameClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                        renameMode = true
                    }
                },
                onPinClick = { /* 플레이리스트 고정하기 */ },
                onDeleteClick = {
                    deletePlaylistDialog.launch()
                    showSheet = false
                },
                onDismissRequest = { showSheet = false },
            )
        }
    }

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
                scrollBehavior = topBarScrollBehavior,
            )

        },
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPlaylistClicked(it.id) }
                            .combinedClickable(
                                onClick = {
                                    println("on Click")
                                    onPlaylistClicked(it.id)
                                },
                                onLongClick = {
                                    println("Long Click")
                                    selectedPlaylist = it
                                    showSheet = true
                                },
                            ),
                        it,
                        playlistPlaybackActions = playlistPlaybackActions,
                        inRenameMode = currentRenameId == it.id,
                        onEnableRenameMode = { currentRenameId = it.id },
                        { name -> onRenamePlaylist(it.id, name); currentRenameId = null },
                        { deletePlaylistDialog.launch() },
                    )
                    if (it != list.last()) {
                        Divider(
                            Modifier
                                .fillMaxWidth()
                                .padding(start = (12 + 36 + 8).dp),
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
    onDelete: () -> Unit,
) {
    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        PlaylistInfoRow(
            modifier = Modifier.weight(1f),
            playlistInfo = playlistInfo,
            inRenameMode, onRename, onEnableRenameMode,
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