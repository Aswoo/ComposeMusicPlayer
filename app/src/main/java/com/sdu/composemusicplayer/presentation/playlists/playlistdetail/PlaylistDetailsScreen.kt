package com.sdu.composemusicplayer.presentation.playlists.playlistdetail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sdu.composemusicplayer.domain.model.Music
import com.sdu.composemusicplayer.domain.model.toSongAlbumArtModel
import com.sdu.composemusicplayer.presentation.component.LocalCommonMusicAction
import com.sdu.composemusicplayer.presentation.component.RenameAbleTextView
import com.sdu.composemusicplayer.presentation.component.menu.buildCommonMusicActions
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.AlbumArtImage
import com.sdu.composemusicplayer.utils.millisToTime


@Composable
fun PlaylistDetailScreen(
    modifier: Modifier,
    onBackPressed: () -> Unit,
    playlistDetailViewModel: PlaylistDetailViewModel = hiltViewModel()
) {

    val state by playlistDetailViewModel.state.collectAsState()

    LaunchedEffect(key1 = state) {
        if (state is PlaylistDetailScreenState.Deleted) {
            onBackPressed()
        }
    }

    if (state is PlaylistDetailScreenState.Deleted) {
        return
    }

    PlaylistDetailScreen(
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
private fun PlaylistDetailScreen(
    modifier: Modifier,
    state: PlaylistDetailScreenState,
    playlistActions: PlaylistActions,
    onBackPressed: () -> Unit,
    onSongClicked: (Music) -> Unit,
    onEdit: () -> Unit,
) {

    if (state is PlaylistDetailScreenState.Loading) return
    val state = state as PlaylistDetailScreenState.Loaded

    val scope = rememberCoroutineScope()
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val listState = rememberLazyListState()
    val shouldShowTopBarTitle by rememberShouldShowTopBar(listState = listState)
    val commonSongsActions = LocalCommonMusicAction.current

    val deletePlaylistDialog =
        rememberDeletePlaylistDialog(playlistName = state.name, onDelete = playlistActions::delete)

    val context = LocalContext.current
    var inRenameMode by remember { mutableStateOf(false) }
    BackHandler(inRenameMode) {
        inRenameMode = false
    }


    Scaffold(
        modifier = modifier.pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    if (inRenameMode) inRenameMode = false
                }
            )
        }
        ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = paddingValues.calculateTopPadding())
                .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
            state = listState,
        ) {
            item {
                PlaylistHeader(
                    Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Min)
                        .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                    state.name,
                    state.music.size,
                    state.music.sumOf { it.duration },
                    state.music.firstOrNull(),
                    inRenameMode = inRenameMode,
                    onRename = { inRenameMode = false; playlistActions.rename(it) },
                    onEnableRenameMode = { inRenameMode = true },
                    playlistActions::play,
                    playlistActions::shuffle
                )
            }

            if (state.music.size == 0) {
                item {
                    EmptyPlaylist(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(paddingValues)
                    )
                }
                return@LazyColumn
            }

            item {
                Text(
                    text = "Songs",
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(
                        top = 16.dp,
                        bottom = 8.dp,
                        start = 16.dp,
                        end = 16.dp
                    )
                )
            }
            itemsIndexed(state.music) { index, item ->

                PlayListMusicRow(
                    modifier = Modifier.fillMaxWidth().clickable {
                        onSongClicked(item)
                    },
                    menuOptions = buildCommonMusicActions(
                        music = item,
                        context = context,
                        shareAction = commonSongsActions.shareAction,
                        songDeleteAction = commonSongsActions.deleteAction
                    ),
                    songRowState = SongRowState.SELECTION_STATE_NOT_SELECTED,
                    music = item
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
    firstMusic: Music?, // for the playlist image,
    inRenameMode: Boolean,
    onRename: (String) -> Unit,
    onEnableRenameMode: () -> Unit,
    onPlay: () -> Unit,
    onShuffle: () -> Unit
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        if (firstMusic != null) {
            AlbumArtImage(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .aspectRatio(1.0f)
                    .weight(0.4f),
                songAlbumArtModel = firstMusic.toSongAlbumArtModel(),
                crossFadeDuration = 150
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(
            Modifier
                .weight(0.6f)
                .fillMaxHeight(), verticalArrangement = Arrangement.SpaceBetween
        ) {

            Column {
                RenameAbleTextView (
                    modifier = Modifier,
                    inRenameMode = inRenameMode,
                    text = name,
                    fontSize = 24,
                    fontWeight = FontWeight.Bold,
                    onEnableRenameMode = onEnableRenameMode,
                    onRename = onRename,
                    enableLongPressToEdit = true
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "$numberOfMusic tracks • ${songsDuration.millisToTime()}",
                    fontWeight = FontWeight.Normal,
                    fontSize = 12.sp
                )
            }
            Spacer(modifier = Modifier.heightIn(6.dp, Dp.Unspecified))
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    modifier = Modifier.weight(0.7f),
                    onClick = onPlay,
                    enabled = numberOfMusic > 0
                ) {
                    Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = "Play")
                }
                Spacer(modifier = Modifier.width(4.dp))
                Button(
                    modifier = Modifier.weight(0.3f),
                    onClick = onShuffle,
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                    enabled = numberOfMusic > 0
                ) {
                    Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = "Shuffle")
                }
            }

        }

    }

}

@Composable
fun rememberShouldShowTopBar(
    listState: LazyListState
): State<Boolean> {
    return remember {
        derivedStateOf {
            if (listState.layoutInfo.visibleItemsInfo.isEmpty()) return@derivedStateOf false
            if (listState.firstVisibleItemIndex != 0) true
            else {
                val visibleItems = listState.layoutInfo.visibleItemsInfo
                listState.firstVisibleItemScrollOffset > visibleItems[0].size * 0.8f
            }
        }
    }
}

@Composable
fun EmptyPlaylist(
    modifier: Modifier
) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                modifier = Modifier.size(72.dp),
                imageVector = Icons.Filled.PlaylistAdd,
                contentDescription = null
            )
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = "Your playlist is empty!\nAdd songs from the main page.",
                fontWeight = FontWeight.Light, fontSize = 16.sp
            )
        }
    }
}


