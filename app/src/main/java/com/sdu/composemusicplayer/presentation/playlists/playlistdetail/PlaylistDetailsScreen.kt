package com.sdu.composemusicplayer.presentation.playlists.playlistdetail

import androidx.activity.compose.BackHandler
import androidx.annotation.VisibleForTesting
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
import com.sdu.composemusicplayer.domain.model.toMusicAlbumArtModel
import com.sdu.composemusicplayer.presentation.component.LocalCommonMusicAction
import com.sdu.composemusicplayer.presentation.component.RenameAbleTextView
import com.sdu.composemusicplayer.presentation.component.menu.buildPlayListMusicActions
import com.sdu.composemusicplayer.presentation.component.menu.removeFromPlaylist
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.AlbumArtImage
import com.sdu.composemusicplayer.ui.theme.SpotiGreen
import com.sdu.composemusicplayer.utils.millisToTime

private val ALBUM_ART_SHAPE_RADIUS = 12.dp
private val ALBUM_ART_SIZE = 100.dp
private const val ALBUM_ART_CROSSFADE_DURATION = 150
private val HEADER_HORIZONTAL_SPACING = 16.dp
private const val RENAME_TEXT_FONT_SIZE = 24
private val RENAME_TEXT_VERTICAL_SPACING = 6.dp
private val TRACKS_INFO_FONT_SIZE = 13.sp
private val ACTION_BUTTONS_VERTICAL_SPACING = 12.dp
private const val PLAY_BUTTON_SHAPE_RADIUS = 50
private const val SCROLL_THRESHOLD_RATIO = 0.8f
private val PLAY_BUTTON_ICON_SPACING = 4.dp
private val ACTION_BUTTONS_HORIZONTAL_SPACING = 8.dp
private val SHUFFLE_BUTTON_SIZE = 48.dp

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
        onSongClicked = playlistDetailViewModel::onSongClicked,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@VisibleForTesting
@Composable
@Suppress("LongMethod", "LongParameterList")
internal fun PlaylistDetailContent(
    modifier: Modifier,
    state: PlaylistDetailScreenState,
    playlistActions: PlaylistActions,
    onSongClicked: (Music) -> Unit,
) {
    if (state is PlaylistDetailScreenState.Loading) return
    val loadedState = state as PlaylistDetailScreenState.Loaded
    val topBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val listState = rememberLazyListState()
    // Removed unused shouldShowTopBarTitle
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
    ) {
        LazyColumn(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = 0.dp)
                    .nestedScroll(topBarScrollBehavior.nestedScrollConnection),
            state = listState,
        ) {
            item {
                PlaylistHeader(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                    info =
                        PlaylistHeaderInfo(
                            name = loadedState.name,
                            numberOfMusic = loadedState.music.size,
                            songsDuration = loadedState.music.sumOf { it.duration },
                            firstMusic = loadedState.music.firstOrNull(),
                            inRenameMode = inRenameMode,
                        ),
                    actions =
                        PlaylistHeaderActions(
                            onRename = {
                                inRenameMode = false
                                playlistActions.rename(it)
                            },
                            onEnableRenameMode = { inRenameMode = true },
                            onPlay = playlistActions::play,
                            onShuffle = playlistActions::shuffle,
                        ),
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
            itemsIndexed(items = loadedState.music, key = { index, music -> music.audioId }) { _, music ->
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

data class PlaylistHeaderInfo(
    val name: String,
    val numberOfMusic: Int,
    val songsDuration: Long,
    val firstMusic: Music?,
    val inRenameMode: Boolean,
)

data class PlaylistHeaderActions(
    val onRename: (String) -> Unit,
    val onEnableRenameMode: () -> Unit,
    val onPlay: () -> Unit,
    val onShuffle: () -> Unit,
)

@Composable
private fun PlaylistHeader(
    modifier: Modifier,
    info: PlaylistHeaderInfo,
    actions: PlaylistHeaderActions,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        info.firstMusic?.let {
            AlbumArtImage(
                modifier =
                    Modifier
                        .clip(RoundedCornerShape(ALBUM_ART_SHAPE_RADIUS))
                        .size(ALBUM_ART_SIZE),
                songAlbumArtModel = it.toMusicAlbumArtModel(),
                crossFadeDuration = ALBUM_ART_CROSSFADE_DURATION,
            )
        }
        Spacer(modifier = Modifier.width(HEADER_HORIZONTAL_SPACING))
        Column(modifier = Modifier.weight(1f)) {
            RenameAbleTextView(
                modifier = Modifier.fillMaxWidth(),
                inRenameMode = info.inRenameMode,
                text = info.name,
                fontSize = RENAME_TEXT_FONT_SIZE,
                fontWeight = FontWeight.Bold,
                onEnableRenameMode = actions.onEnableRenameMode,
                onRename = actions.onRename,
                enableLongPressToEdit = true,
            )
            Spacer(modifier = Modifier.height(RENAME_TEXT_VERTICAL_SPACING))
            Text(
                text = "${info.numberOfMusic} tracks • ${info.songsDuration.millisToTime()}",
                fontSize = TRACKS_INFO_FONT_SIZE,
                color = Color.LightGray,
            )
            Spacer(modifier = Modifier.height(ACTION_BUTTONS_VERTICAL_SPACING))
            PlaylistActionsButtons(
                modifier = Modifier.fillMaxWidth(),
                onPlay = actions.onPlay,
                onShuffle = actions.onShuffle,
                enabled = info.numberOfMusic > 0,
            )
        }
    }
}

@Composable
private fun PlaylistActionsButtons(
    modifier: Modifier,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    enabled: Boolean,
) {
    Row(modifier = modifier.fillMaxWidth()) {
        Button(
            modifier = Modifier.weight(1f),
            onClick = onPlay,
            colors = ButtonDefaults.buttonColors(containerColor = SpotiGreen),
            shape = RoundedCornerShape(PLAY_BUTTON_SHAPE_RADIUS.toFloat()),
            enabled = enabled,
        ) {
            Icon(imageVector = Icons.Rounded.PlayArrow, contentDescription = null)
            Spacer(modifier = Modifier.width(PLAY_BUTTON_ICON_SPACING))
            Text("Play")
        }
        Spacer(modifier = Modifier.width(ACTION_BUTTONS_HORIZONTAL_SPACING))
        Button(
            modifier = Modifier.size(SHUFFLE_BUTTON_SIZE),
            onClick = onShuffle,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
            contentPadding = PaddingValues(0.dp),
            enabled = enabled,
        ) {
            Icon(imageVector = Icons.Rounded.Shuffle, contentDescription = null)
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
                    listState.firstVisibleItemScrollOffset >
                    listState.layoutInfo.visibleItemsInfo[0].size * SCROLL_THRESHOLD_RATIO
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
