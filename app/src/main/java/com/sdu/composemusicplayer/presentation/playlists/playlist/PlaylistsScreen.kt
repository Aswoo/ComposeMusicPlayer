@file:OptIn(ExperimentalFoundationApi::class, ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.sdu.composemusicplayer.presentation.playlists.playlist

import PlaylistBottomSheet
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import com.sdu.composemusicplayer.presentation.component.dialog.rememberCreatePlaylistDialog
import com.sdu.composemusicplayer.presentation.playlists.playlistdetail.rememberDeletePlaylistDialog
import com.sdu.composemusicplayer.ui.theme.SpotiBackground
import com.sdu.composemusicplayer.ui.theme.SpotiDarkGray
import com.sdu.composemusicplayer.ui.theme.SpotiDivider
import com.sdu.composemusicplayer.ui.theme.SpotiGray
import com.sdu.composemusicplayer.ui.theme.SpotiGreen
import com.sdu.composemusicplayer.ui.theme.SpotiWhite
import kotlinx.coroutines.launch

private val DIVIDER_START_PADDING = 56.dp
private val DRAG_HANDLE_WIDTH = 40.dp
private val DRAG_HANDLE_HEIGHT = 4.dp
private val DRAG_HANDLE_SHAPE_RADIUS = 2.dp
private val DRAG_HANDLE_VERTICAL_PADDING = 8.dp

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
        onPlaylistClicked = onNavigateToPlaylist,
        onDeletePlaylist = playlistsViewModel::onDelete,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistsScreen(
    modifier: Modifier,
    state: PlaylistsScreenState,
    onPlaylistClicked: (Int) -> Unit,
    onDeletePlaylist: (Int) -> Unit,
) {
    val createPlaylistsDialog = rememberCreatePlaylistDialog()
    val coroutineScope = rememberCoroutineScope()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedPlaylist by remember { mutableStateOf<PlaylistInfo?>(null) }
    var renameMode by remember { mutableStateOf(false) }
    val deletePlaylistDialog =
        rememberDeletePlaylistDialog(
            playlistName = selectedPlaylist?.name ?: "",
        ) {
            selectedPlaylist?.id?.let { onDeletePlaylist(it) }
            selectedPlaylist = null
        }

    var showSheet by remember { mutableStateOf(false) }

    PlaylistManagementBottomSheet(
        config =
            PlaylistManagementBottomSheetConfig(
                showSheet = showSheet,
                sheetState = sheetState,
                selectedPlaylist = selectedPlaylist,
                onDismissRequest = { showSheet = false },
                onRenameClick = {
                    coroutineScope.launch {
                        sheetState.hide()
                        renameMode = true
                    }
                },
                onPinClick = { /* TODO */ },
                onDeleteClick = {
                    deletePlaylistDialog.launch()
                    showSheet = false
                },
            ),
    )

    PlaylistsScreenScaffold(
        modifier = modifier,
        topBarScrollBehavior = topBarScrollBehavior,
        onCreatePlaylist = { createPlaylistsDialog.launch() },
    ) { paddingValues ->
        PlaylistsScreenContent(
            modifier = Modifier.padding(top = paddingValues.calculateTopPadding()),
            state = state,
            topBarScrollBehavior = topBarScrollBehavior,
            onPlaylistClicked = onPlaylistClicked,
            onPlaylistLongClicked = {
                selectedPlaylist = it
                showSheet = true
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
data class PlaylistManagementBottomSheetConfig(
    val showSheet: Boolean,
    val sheetState: SheetState,
    val selectedPlaylist: PlaylistInfo?,
    val onDismissRequest: () -> Unit,
    val onRenameClick: () -> Unit,
    val onPinClick: () -> Unit,
    val onDeleteClick: () -> Unit,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistManagementBottomSheet(config: PlaylistManagementBottomSheetConfig) {
    if (config.showSheet) {
        ModalBottomSheet(
            onDismissRequest = config.onDismissRequest,
            sheetState = config.sheetState,
            containerColor = SpotiDarkGray,
            dragHandle = {
                Box(
                    Modifier
                        .padding(vertical = DRAG_HANDLE_VERTICAL_PADDING)
                        .size(width = DRAG_HANDLE_WIDTH, height = DRAG_HANDLE_HEIGHT)
                        .background(SpotiGray, shape = RoundedCornerShape(DRAG_HANDLE_SHAPE_RADIUS)),
                )
            },
        ) {
            PlaylistBottomSheet(
                playlistName = config.selectedPlaylist?.name ?: "이름없는 플레이리스트",
                onRenameClick = config.onRenameClick,
                onPinClick = config.onPinClick,
                onDeleteClick = config.onDeleteClick,
                onDismissRequest = config.onDismissRequest,
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistsScreenScaffold(
    modifier: Modifier,
    topBarScrollBehavior: TopAppBarScrollBehavior,
    onCreatePlaylist: () -> Unit,
    content: @Composable (PaddingValues) -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = SpotiBackground,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Playlists",
                        fontWeight = FontWeight.SemiBold,
                        color = SpotiWhite,
                    )
                },
                actions = {
                    IconButton(onClick = onCreatePlaylist) {
                        Icon(
                            imageVector = Icons.Rounded.Add,
                            contentDescription = null,
                            tint = SpotiWhite,
                        )
                    }
                },
                scrollBehavior = topBarScrollBehavior,
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Black,
                        titleContentColor = SpotiWhite,
                        actionIconContentColor = SpotiWhite,
                    ),
            )
        },
        content = content,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PlaylistsScreenContent(
    modifier: Modifier,
    state: PlaylistsScreenState,
    topBarScrollBehavior: TopAppBarScrollBehavior,
    onPlaylistClicked: (Int) -> Unit,
    onPlaylistLongClicked: (PlaylistInfo) -> Unit,
) {
    if (state is PlaylistsScreenState.Loading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator(color = SpotiGreen)
        }
    } else {
        val list = (state as PlaylistsScreenState.Success).playlists

        LazyColumn(
            modifier =
                modifier
                    .fillMaxSize()
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
        ) {
            item {
                Divider(Modifier.fillMaxWidth(), color = SpotiDivider)
            }

            items(list) {
                var currentRenameId by remember { mutableStateOf<Int?>(null) }
                BackHandler(currentRenameId != null) {
                    currentRenameId = null
                }
                PlaylistRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .combinedClickable(
                                onClick = { onPlaylistClicked(it.id) },
                                onLongClick = { onPlaylistLongClicked(it) },
                            ),
                    playlistInfo = it,
                )

                if (it != list.last()) {
                    Divider(
                        Modifier
                            .fillMaxWidth()
                            .padding(start = DIVIDER_START_PADDING),
                        color = SpotiDivider,
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
) {
    Row(
        modifier
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlaylistInfoRow(
            modifier = Modifier.weight(1f),
            playlistInfo = playlistInfo,
        )
        Spacer(modifier = Modifier.width(8.dp))
    }
}
