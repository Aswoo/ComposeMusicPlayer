package com.sdu.composemusicplayer.presentation.playlists.playlistdetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlaylistAdd
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.toSongAlbumArtModel
import com.sdu.composemusicplayer.presentation.component.LocalCommonMusicAction
import com.sdu.composemusicplayer.presentation.component.RenameAbleTextView
import com.sdu.composemusicplayer.presentation.component.menu.buildPlayListMusicActions
import com.sdu.composemusicplayer.presentation.component.menu.removeFromPlaylist
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.AlbumArtImage
import com.sdu.composemusicplayer.ui.theme.SpotiGreen
import com.sdu.composemusicplayer.utils.millisToTime

@Composable
fun PlaylistDetailScreen(
    modifier: Modifier,
    onBackPressed: () -> Unit,
    playlistDetailViewModel: PlaylistDetailViewModel = hiltViewModel(),
) {
    val state by playlistDetailViewModel.state.collectAsState()
    LaunchedEffect(state) {
        if (state is PlaylistDetailScreenState.Deleted) onBackPressed()
    }
    if (state is PlaylistDetailScreenState.Deleted) return
    PlaylistDetailContent(
        modifier = modifier,
        state = state,
        playlistActions = playlistDetailViewModel,
        onBackPressed = onBackPressed,
        onSongClicked = playlistDetailViewModel::onSongClicked,
        onEdit = {},
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlaylistDetailContent(
    modifier: Modifier,
    state: PlaylistDetailScreenState,
    playlistActions: PlaylistActions,
    onBackPressed: () -> Unit,
    onSongClicked: (Music) -> Unit,
    onEdit: () -> Unit,
) {
    if (state is PlaylistDetailScreenState.Loading) return
    val loadedState = state as PlaylistDetailScreenState.Loaded
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    val shouldShowTopBarTitle by rememberShouldShowTopBar(listState = listState)
    val commonSongsActions = LocalCommonMusicAction.current
    val context = LocalContext.current
    var inRenameMode by remember { mutableStateOf(false) }
    BackHandler(inRenameMode) { inRenameMode = false }

    Scaffold(
        modifier =
            modifier
                .background(Color.Black)
                .pointerInput(Unit) {
                    detectTapGestures { if (inRenameMode) inRenameMode = false }
                },
        containerColor = Color.Black,
    ) { paddingValues ->
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
            state = listState,
        ) {
            item {
                PlaylistHeader(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    name = loadedState.name,
                    numberOfMusic = loadedState.music.size,
                    songsDuration = loadedState.music.sumOf { it.duration },
                    firstMusic = loadedState.music.firstOrNull(),
                    inRenameMode = inRenameMode,
                    onRename = {
                        inRenameMode = false
                        playlistActions.rename(it)
                    },
                    onEnableRenameMode = { inRenameMode = true },
                    onPlay = playlistActions::play,
                    onShuffle = playlistActions::shuffle,
                )
            }
            if (loadedState.music.isEmpty()) {
                item {
                    EmptyPlaylist(modifier = Modifier.fillParentMaxSize())
                }
                return@LazyColumn
            }
            item {
                Text(
                    text = "Tracks",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                )
            }
            itemsIndexed(loadedState.music) { _, music ->
                PlayListMusicRow(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onSongClicked(music) },
                    music = music,
                    isPlaying =
                        if (state.currentPlayingMusic != null &&
                            state.currentPlayingMusic.audioId == music.audioId
                        ) {
                            true
                        } else {
                            false
                        },
                    menuOptions =
                        buildPlayListMusicActions(
                            music = music,
                            context = context,
                            shareAction = commonSongsActions.shareAction,
                        ).apply {
                            removeFromPlaylist {
                                playlistActions.removeSongs(listOf(music.audioPath))
                            }
                        },
                )
            }
        }
    }
}

@Composable
private fun PlaylistHeader(
    modifier: Modifier,
    name: String,
    numberOfMusic: Int,
    songsDuration: Long,
    firstMusic: Music?,
    inRenameMode: Boolean,
    onRename: (String) -> Unit,
    onEnableRenameMode: () -> Unit,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        firstMusic?.let {
            AlbumArtImage(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .size(100.dp),
                songAlbumArtModel = it.toSongAlbumArtModel(),
                crossFadeDuration = 150,
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            RenameAbleTextView(
                modifier = Modifier.fillMaxWidth(),
                inRenameMode = inRenameMode,
                text = name,
                fontSize = 24,
                fontWeight = FontWeight.Bold,
                onEnableRenameMode = onEnableRenameMode,
                onRename = onRename,
                enableLongPressToEdit = true,
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "$numberOfMusic tracks • ${songsDuration.millisToTime()}",
                fontSize = 13.sp,
                color = Color.LightGray,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier.weight(1f),
                    onClick = onPlay,
                    colors = ButtonDefaults.buttonColors(containerColor = SpotiGreen),
                    shape = RoundedCornerShape(50),
                    enabled = numberOfMusic > 0,
                ) {
                    Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Play")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    modifier = Modifier.size(48.dp),
                    onClick = onShuffle,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    contentPadding = PaddingValues(0.dp),
                    enabled = numberOfMusic > 0,
                ) {
                    Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null)
                }
            }
        }
    }
}

@Composable
fun rememberShouldShowTopBar(listState: androidx.compose.foundation.lazy.LazyListState): State<Boolean> =
    remember {
        derivedStateOf {
            if (listState.layoutInfo.visibleItemsInfo.isEmpty()) {
                false
            } else {
                listState.firstVisibleItemIndex > 0 ||
                    listState.firstVisibleItemScrollOffset > listState.layoutInfo.visibleItemsInfo[0].size * 0.8f
            }
        }
    }

@Composable
fun EmptyPlaylist(modifier: Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier = Modifier.size(72.dp),
                imageVector = Icons.Filled.PlaylistAdd,
                contentDescription = null,
                tint = Color.Gray,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Your playlist is empty!\nAdd songs from the main page.",
                fontSize = 14.sp,
                color = Color.LightGray,
                fontWeight = FontWeight.Light,
            )
        }
    }
}
