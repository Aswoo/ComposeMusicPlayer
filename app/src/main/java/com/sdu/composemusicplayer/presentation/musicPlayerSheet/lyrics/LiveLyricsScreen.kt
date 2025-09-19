@file:Suppress("ktlint:standard:no-wildcard-imports", "LongParameterList", "TooManyFunctions")

package com.sdu.composemusicplayer.presentation.musicPlayerSheet.lyrics

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import androidx.hilt.navigation.compose.hiltViewModel
import com.sdu.composemusicplayer.core.model.lyrics.LyricsFetchSource
import com.sdu.composemusicplayer.core.model.lyrics.PlainLyrics
import com.sdu.composemusicplayer.core.model.lyrics.SynchronizedLyrics
import com.sdu.composemusicplayer.utils.AppStateUtil.getNavigationBarHeightDp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val DARK_BACKGROUND_COLOR = 0xFF121212
private const val DARK_GRAY_COLOR = 0xFF1E1E1E
private const val LIGHT_GRAY_ALPHA = 0.5f
private const val CURRENT_LINE_ALPHA = 0.7f
private const val PAST_LINE_ALPHA = 0.5f
private const val SYNC_DELAY = 100L
private const val SCROLL_OFFSET_FACTOR = 2

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveLyricsScreen(
    onSwap: () -> Unit, // 이 콜백은 화면 전환과 관련될 수 있으므로 유지
    onBack: () -> Unit, // 화면 닫기와 관련될 수 있으므로 유지
    lyricsViewModel: LiveLyricsViewModel = hiltViewModel(),
) {
    // ViewModel의 uiState를 구독하여 LiveLyricsUiState를 가져옵니다.
    val uiState by lyricsViewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            LyricsScreenTopAppBar(
                // LiveLyricsUiState에서 직접 값을 사용합니다.
                songTitle = uiState.currentPlayedMusic?.title ?: "Not Playing",
                artistName = uiState.currentPlayedMusic?.artist ?: "",
                onBack = onBack,
                onSwap = onSwap,
            )
        },
        bottomBar = {
            // 가사 상태에 따라 BottomControls 표시 여부 결정
            if (uiState.lyricsScreenState is LyricsScreenState.SyncedLyrics ||
                uiState.lyricsScreenState is LyricsScreenState.TextLyrics
            ) {
                LyricsScreenBottomControls(
                    // LiveLyricsUiState에서 직접 값을 사용합니다.
                    isPlaying = uiState.isPlaying,
                    onPlayPause = lyricsViewModel::togglePlayPause,
                    progress = uiState.currentProgress,
                    onProgressChange = lyricsViewModel::seekToProgress,
                    currentTime = uiState.currentTimeDisplay,
                    totalTime = uiState.totalTimeDisplay,
                    lyricsFetchSource = uiState.lyricsSourceForMenu,
                    onSaveLyricsToSongFile = lyricsViewModel::saveExternalLyricsToSongFile,
                    lyricsTextToCopy = uiState.lyricsTextForCopy,
                )
            }
        },
        containerColor = Color(DARK_BACKGROUND_COLOR.toLong()), // 어두운 배경색
    ) { paddingValues ->
        LiveLyricsContent(
            modifier =
            Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            // LiveLyricsUiState에서 직접 값을 사용합니다.
            lyricsScreenState = uiState.lyricsScreenState,
            songProgressMillis = lyricsViewModel::getCurrentSongProgressMillis, // SyncedLyricsState에 필요
            onSeekToPositionMillis = lyricsViewModel::setSongProgressMillis, // SyncedLyricsState에 필요
            onRetry = lyricsViewModel::retryLoadLyrics,
            onBack = onBack, // NoLyricsState에 필요
        )
    }
}

@Composable
fun LiveLyricsContent(
    modifier: Modifier,
    // LiveLyricsScreenState는 LiveLyricsUiState의 일부입니다.
    lyricsScreenState: LyricsScreenState,
    songProgressMillis: () -> Long,
    onSeekToPositionMillis: (Long) -> Unit,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Box(modifier = modifier) {
        when (lyricsScreenState) {
            is LyricsScreenState.NoLyrics ->
                NoLyricsState(
                    modifier = Modifier.align(Alignment.Center),
                    reason = lyricsScreenState.reason,
                    onRetry = onRetry,
                    onBack = onBack,
                )

            is LyricsScreenState.Loading, is LyricsScreenState.SearchingLyrics ->
                LoadingState(modifier = Modifier.align(Alignment.Center))

            is LyricsScreenState.NotPlaying ->
                NotPlayingState(modifier = Modifier.align(Alignment.Center))

            is LyricsScreenState.TextLyrics ->
                PlainLyricsState(
                    modifier = Modifier.fillMaxSize(),
                    plainLyrics = lyricsScreenState.plainLyrics,
                )

            is LyricsScreenState.SyncedLyrics ->
                SyncedLyricsState(
                    modifier = Modifier.fillMaxSize(),
                    synchronizedLyrics = lyricsScreenState.syncedLyrics,
                    onSeekToPositionMillis = onSeekToPositionMillis,
                    songProgressMillis = songProgressMillis,
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LyricsScreenTopAppBar(
    songTitle: String,
    artistName: String,
    onBack: () -> Unit,
    onSwap: () -> Unit,
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = songTitle,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                )
                Text(
                    text = artistName,
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    maxLines = 1,
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Back",
                    tint = Color.White,
                )
            }
        },
        actions = {
            IconButton(onClick = onSwap) {
                Icon(
                    imageVector = Icons.Filled.SwapHoriz,
                    contentDescription = "Swap Lyrics Source",
                    tint = Color.White,
                )
            }
        },
        colors =
        TopAppBarDefaults.topAppBarColors(
            containerColor = Color(DARK_GRAY_COLOR.toLong()),
        ),
    )
}

@Composable
@Suppress("LongParameterList", "LongMethod")
fun LyricsScreenBottomControls(
    isPlaying: Boolean,
    onPlayPause: () -> Unit,
    progress: Float,
    onProgressChange: (Float) -> Unit,
    currentTime: String,
    totalTime: String,
    lyricsFetchSource: LyricsFetchSource?,
    onSaveLyricsToSongFile: () -> Unit,
    lyricsTextToCopy: String,
) {
    val localClipboardManager = LocalClipboardManager.current
    var showMoreActions by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val navBarHeightDp = getNavigationBarHeightDp(context.resources)

    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .background(Color(DARK_GRAY_COLOR.toLong()))
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .padding(bottom = navBarHeightDp.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = currentTime, color = Color.LightGray, fontSize = 12.sp)
            Slider(
                value = progress,
                onValueChange = onProgressChange,
                modifier =
                Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                colors =
                SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray.copy(alpha = LIGHT_GRAY_ALPHA),
                ),
            )
            Text(text = totalTime, color = Color.LightGray, fontSize = 12.sp)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = { localClipboardManager.setText(AnnotatedString(lyricsTextToCopy)) }) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Copy Lyrics",
                    tint = Color.White,
                )
            }

            IconButton(onClick = onPlayPause, modifier = Modifier.size(64.dp)) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.PauseCircleFilled else Icons.Filled.PlayCircleFilled,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.White,
                    modifier = Modifier.fillMaxSize(),
                )
            }

            Box {
                IconButton(onClick = { showMoreActions = !showMoreActions }) {
                    Icon(
                        imageVector = Icons.Filled.MoreVert,
                        contentDescription = "More Actions",
                        tint = Color.White,
                    )
                }
                DropdownMenu(
                    expanded = showMoreActions,
                    onDismissRequest = { showMoreActions = false },
                    modifier = Modifier.background(Color(DARK_GRAY_COLOR.toLong())),
                ) {
                    if (lyricsFetchSource == LyricsFetchSource.EXTERNAL) {
                        DropdownMenuItem(
                            text = { Text("Save to Song File", color = Color.White) },
                            onClick = {
                                onSaveLyricsToSongFile()
                                showMoreActions = false
                            },
                        )
                    }
                    // 다른 액션 추가 가능
                }
            }
        }
    }
}

@Composable
@Suppress("LongParameterList")
fun LyricLine(
    modifier: Modifier,
    line: String,
    isCurrentLine: Boolean = false,
    isPastLine: Boolean = false,
    isShowingContextMenu: Boolean = false,
) {
    val context = LocalContext.current
    val localClipboardManager = LocalClipboardManager.current

    val textColor =
        when {
            isCurrentLine -> Color.White
            isPastLine -> Color.Gray.copy(alpha = CURRENT_LINE_ALPHA)
            else -> Color.Gray.copy(alpha = PAST_LINE_ALPHA)
        }
    val fontSize = if (isCurrentLine) 26.sp else 24.sp
    val fontWeight = if (isCurrentLine) FontWeight.Bold else FontWeight.Normal

    Box(modifier = modifier) {
        if (isShowingContextMenu) {
            // 기존 컨텍스트 메뉴 로직 (예: Popup 사용)
            // LineContextMenu(...) 또는 Popup { ... }
        }
        Text(
            modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .then(if (isShowingContextMenu) Modifier.shimmerLoadingAnimation() else Modifier),
            text = line,
            fontSize = fontSize,
            fontWeight = fontWeight,
            color = textColor,
            lineHeight = 32.sp,
        )
    }
}

fun Modifier.fadingEdge(brush: Brush) =
    this
        .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
        .drawWithContent {
            drawContent()
            drawRect(brush = brush, blendMode = BlendMode.DstIn)
        }

@Composable
fun NoLyricsState(
    modifier: Modifier,
    reason: NoLyricsReason,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = modifier.padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        when (reason) {
            NoLyricsReason.NOT_FOUND -> {
                Text(text = "No lyrics available", color = Color.White, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(DARK_GRAY_COLOR.toLong())),
                ) {
                    Text(text = "Go Back", color = Color.White)
                }
            }
            NoLyricsReason.NETWORK_ERROR -> {
                Text(text = "Check your network connection", color = Color.White, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRetry,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(DARK_GRAY_COLOR.toLong())),
                ) {
                    Text(text = "Try Again", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun LoadingState(modifier: Modifier) {
    Box(modifier = modifier.testTag("LoadingIndicator"), contentAlignment = Alignment.Center) {
        CircularProgressIndicator(color = Color.White)
    }
}

@Composable
fun NotPlayingState(modifier: Modifier) {
    Box(modifier = modifier.padding(16.dp), contentAlignment = Alignment.Center) {
        Text(text = "No song is being played.", color = Color.White, fontSize = 18.sp)
    }
}

@Composable
fun PlainLyricsState(
    modifier: Modifier,
    plainLyrics: PlainLyrics,
) {
    val itemsSpacing = 8.dp
    var contextMenuShownIndex by remember { mutableStateOf(-1) }
    val vibrationManager = LocalHapticFeedback.current

    LazyColumn(
        modifier = modifier.padding(horizontal = 20.dp, vertical = 16.dp),
        contentPadding = PaddingValues(bottom = itemsSpacing),
    ) {
        itemsIndexed(plainLyrics.lines) { index, s ->
            LyricLine(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                contextMenuShownIndex = index
                                vibrationManager.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                        ) {}
                    },
                line = s,
                isCurrentLine = true, // PlainLyrics에서는 항상 현재 라인처럼 표시
                isShowingContextMenu = index == contextMenuShownIndex,
            )
            Spacer(modifier = Modifier.height(itemsSpacing))
        }
    }
}

@Composable
fun SyncedLyricsState(
    modifier: Modifier,
    synchronizedLyrics: SynchronizedLyrics,
    onSeekToPositionMillis: (Long) -> Unit,
    songProgressMillis: () -> Long,
) {
    var currentLyricIndex by remember(synchronizedLyrics) { mutableStateOf(-1) }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    var listHeightPx by remember { mutableStateOf(0) }
    var contextMenuShownIndex by remember { mutableStateOf(-1) }

    LyricSynchronizerEffect(
        synchronizedLyrics = synchronizedLyrics,
        songProgressMillis = songProgressMillis,
    ) { calculatedIndex ->
        if (currentLyricIndex != calculatedIndex) {
            currentLyricIndex = calculatedIndex
            if (shouldScroll(contextMenuShownIndex, listState, currentLyricIndex)) {
                coroutineScope.launch {
                    scrollToCurrentLyric(listState, currentLyricIndex, listHeightPx)
                }
            }
        }
    }

    val vibrationManager = LocalHapticFeedback.current
    Box(
        modifier =
        modifier
            .onGloballyPositioned { listHeightPx = it.size.height }
            .padding(horizontal = 20.dp),
    ) {
        SyncedLyricsList(
            modifier = Modifier.fillMaxSize(),
            listState = listState,
            synchronizedLyrics = synchronizedLyrics,
            currentLyricIndex = currentLyricIndex,
            contextMenuShownIndex = contextMenuShownIndex,
            onContextMenuChange = { contextMenuShownIndex = it },
            onSeekToPositionMillis = onSeekToPositionMillis
        )
    }
}

@Composable
private fun SyncedLyricsList(
    modifier: Modifier,
    listState: LazyListState,
    synchronizedLyrics: SynchronizedLyrics,
    currentLyricIndex: Int,
    contextMenuShownIndex: Int,
    onContextMenuChange: (Int) -> Unit,
    onSeekToPositionMillis: (Long) -> Unit
) {
    val vibrationManager = LocalHapticFeedback.current
    val itemsSpacing = 8.dp

    LazyColumn(
        modifier = modifier,
        state = listState,
        contentPadding = PaddingValues(vertical = 16.dp),
    ) {
        itemsIndexed(synchronizedLyrics.segments) { index, segment ->
            LyricLine(
                modifier = Modifier
                    .fillMaxWidth()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onLongPress = {
                                onContextMenuChange(index)
                                vibrationManager.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                        ) {
                            onSeekToPositionMillis(segment.durationMillis.toLong())
                        }
                    },
                line = segment.text,
                isCurrentLine = index == currentLyricIndex && contextMenuShownIndex == -1,
                isPastLine = index < currentLyricIndex,
                isShowingContextMenu = index == contextMenuShownIndex,
            )
            Spacer(modifier = Modifier.height(itemsSpacing))
        }
    }
}

private fun shouldScroll(
    contextMenuShownIndex: Int,
    listState: LazyListState,
    currentLyricIndex: Int
): Boolean {
    return contextMenuShownIndex == -1 &&
            listState.layoutInfo.totalItemsCount > 0 &&
            currentLyricIndex >= 0 &&
            currentLyricIndex < listState.layoutInfo.totalItemsCount
}

private suspend fun scrollToCurrentLyric(
    listState: LazyListState,
    currentLyricIndex: Int,
    listHeightPx: Int
) {
    val viewportHeight = listState.layoutInfo.viewportSize.height
    val centerOffset = viewportHeight / SCROLL_OFFSET_FACTOR
    val targetItemInfo = listState.layoutInfo.visibleItemsInfo.find { it.index == currentLyricIndex }

    if (targetItemInfo != null) {
        val currentItemCenter = targetItemInfo.offset + targetItemInfo.size / 2
        if (kotlin.math.abs(currentItemCenter - centerOffset) > targetItemInfo.size) { // 중앙에서 많이 벗어났을 때만 스크롤
            listState.animateScrollToItem(currentLyricIndex, scrollOffset = -centerOffset + targetItemInfo.size / 2)
        }
    } else {
        // 현재 라인이 보이지 않으면 중앙으로 스크롤 (아이템 크기를 알 수 없으므로 대략적인 중앙으로)
        listState.animateScrollToItem(currentLyricIndex, scrollOffset = -(listHeightPx / 2))
    }
}

@Composable
fun LyricSynchronizerEffect(
    synchronizedLyrics: SynchronizedLyrics?,
    songProgressMillis: () -> Long,
    onCurrentLyricIndexChanged: (Int) -> Unit,
) {
    if (synchronizedLyrics == null || synchronizedLyrics.segments.isEmpty()) {
        LaunchedEffect(Unit) {
            onCurrentLyricIndexChanged(-1)
        }
        return
    }

    LaunchedEffect(synchronizedLyrics, songProgressMillis()) {
        var previousCalculatedIndex = -1
        while (isActive) {
            val currentTimeMs = songProgressMillis()
            var currentIndex = -1

            // segments를 역순으로 순회하며 현재 시간보다 시작 시간이 작거나 같은 첫 번째 세그먼트를 찾습니다.
            // 이렇게 하면 가장 마지막으로 시간이 일치하는 가사를 찾게 됩니다.
            for (i in synchronizedLyrics.segments.indices.reversed()) {
                if (synchronizedLyrics.segments[i].durationMillis <= currentTimeMs) {
                    currentIndex = i
                    break // 찾았으면 루프 종료
                }
            }
            // 만약 모든 가사가 현재 시간보다 뒤에 있다면, 첫번째 가사를 선택 (혹은 -1 유지)
            // 혹은, 첫 번째 가사의 시작시간보다 현재시간이 작으면 -1
            if (currentIndex == -1 && synchronizedLyrics.segments.isNotEmpty() &&
                currentTimeMs < synchronizedLyrics.segments.first().durationMillis
            ) {
                // 아무 가사도 해당되지 않음
            } else if (currentIndex == -1 && synchronizedLyrics.segments.isNotEmpty()) {
                // 이 경우는 모든 가사가 현재 시간보다 뒤에 있거나, 모든 가사의 시작시간이 0인 경우 발생 가능
                // 기본적으로 첫 번째 가사 이전은 -1
            }

            if (currentIndex != previousCalculatedIndex) {
                onCurrentLyricIndexChanged(currentIndex)
                previousCalculatedIndex = currentIndex
            }
            delay(SYNC_DELAY) // 동기화 주기
        }
    }
}
