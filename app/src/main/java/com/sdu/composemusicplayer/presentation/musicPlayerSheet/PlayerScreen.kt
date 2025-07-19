package com.sdu.composemusicplayer.presentation.musicPlayerSheet

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FastForward
import androidx.compose.material.icons.rounded.FastRewind
import androidx.compose.material.icons.rounded.PauseCircle
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowHeightSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent.ExpandedMusicPlayerContent
import com.sdu.composemusicplayer.viewmodel.MusicUiState
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel

@Composable
fun PlayerScreen(
    modifier: Modifier,
    nowPlayingBarPadding: PaddingValues,
    barHeight: Dp,
    isExpanded: Boolean,
    onCollapseNowPlaying: () -> Unit,
    onExpandNowPlaying: () -> Unit,
    progressProvider: () -> Float,
    viewModel: PlayerViewModel = hiltViewModel(),
) {
    val focusManager = LocalFocusManager.current
    var showAddToPlaylistDialog by remember { mutableStateOf(false) }
    LaunchedEffect(key1 = isExpanded) {
        if (isExpanded) {
            focusManager.clearFocus(true)
        }
    }

    if (isExpanded) {
        BackHandler(true) {
            onCollapseNowPlaying()
        }
    }

    val uiState by viewModel.uiState.collectAsState()

    if (showAddToPlaylistDialog) {
        AddToPlaylistDialog(
            musicUri = uiState.currentPlayedMusic.audioPath,
            onDismissRequest = { showAddToPlaylistDialog = false },
        )
    }

    PlayerScreen(
        modifier = modifier.clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp)),
        nowPlayingBarPadding = nowPlayingBarPadding,
        uiState = uiState,
        barHeight = barHeight,
        isExpanded = isExpanded,
        onExpandNowPlaying = onExpandNowPlaying,
        progressProvider = progressProvider,
        showAddToPlaylistDialog = { showAddToPlaylistDialog = true },
        viewModel = viewModel,
    )
}

@Composable
internal fun PlayerScreen(
    modifier: Modifier,
    nowPlayingBarPadding: PaddingValues,
    uiState: MusicUiState,
    barHeight: Dp,
    isExpanded: Boolean,
    onExpandNowPlaying: () -> Unit,
    progressProvider: () -> Float,
    showAddToPlaylistDialog: () -> Unit,
    viewModel: PlayerViewModel,
) {
//    val playerTheme = LocalUserPreferences.current.uiSettings.playerThemeUi
//    val isDarkTheme = when (LocalUserPreferences.current.uiSettings.theme) {
//        AppThemeUi.DARK -> true
//        AppThemeUi.LIGHT -> false
//        else -> isSystemInDarkTheme()
//    }
//
//    // Since we use a darker background image for the NowPlaying screen
//    // we need to make the status bar icons lighter
//    if (isExpanded && (isDarkTheme || playerTheme == PlayerThemeUi.BLUR))
//        DarkStatusBarEffect()

    Surface(
        modifier = modifier,
        tonalElevation = if (MaterialTheme.colorScheme.background == Color.Black) 0.dp else 3.dp,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            var isShowingQueue by remember {
                mutableStateOf(false)
            }
            FullScreenNowPlaying(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            alpha = ((progressProvider() - 0.15f) * 2.0f).coerceIn(0.0f, 1.0f)
                        },
                onOpenQueue = showAddToPlaylistDialog,
                onCloseQueue = { isShowingQueue = true },
                progressProvider = progressProvider,
                uiState = uiState,
                playerViewModel = viewModel,
                isShowingQueue = isShowingQueue,
            )
            LaunchedEffect(key1 = isExpanded) {
                if (!isExpanded) isShowingQueue = false
            }
            MiniPlayer(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(barHeight)
                        .padding(nowPlayingBarPadding)
                        .pointerInput(Unit) {
                            detectTapGestures { onExpandNowPlaying() }
                        }.graphicsLayer {
                            alpha = (1 - (progressProvider() * 6.66f).coerceAtMost(1.0f))
                        }.testTag("miniPlayer"),
                state = uiState,
                showExtraControls = true,
                songProgressProvider = {
                    val current = uiState.currentDuration
                    val total = uiState.currentPlayedMusic.duration
                    if (total > 0) current.toFloat() / total.toFloat() else 0f
                },
                enabled = !isExpanded,
                onTogglePlayback = { viewModel.onEvent(PlayerEvent.PlayPause(uiState.isPlaying)) },
                onNext = { viewModel.onEvent(PlayerEvent.Next) },
                onPrevious = { viewModel.onEvent(PlayerEvent.Previous) },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Composable
fun FullScreenNowPlaying(
    modifier: Modifier,
    isShowingQueue: Boolean,
    onCloseQueue: () -> Unit,
    onOpenQueue: () -> Unit,
    progressProvider: () -> Float,
    uiState: MusicUiState,
    playerViewModel: PlayerViewModel,
) {
    val music =
        remember(uiState.currentPlayedMusic) {
            uiState.currentPlayedMusic
        }

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center,
    ) {
        AnimatedVisibility(
            visible = true,
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
//            CrossFadingAlbumArt(
//                modifier = Modifier.fillMaxSize(),
//                songAlbumArtModel = song.toSongAlbumArtModel(),
//                errorPainterType = ErrorPainterType.SOLID_COLOR,
//                blurTransformation = remember { BlurTransformation(radius = 40, scale = 0.15f) },
//                colorFilter = remember {
//                    ColorFilter.tint(
//                        Color(0xFF999999),
//                        BlendMode.Multiply,
//                    )
//                },
//            )
        }

        val activity = LocalContext.current as Activity
        val windowSizeClass = calculateWindowSizeClass(activity = activity)
        val heightClass = windowSizeClass.heightSizeClass
        val widthClass = windowSizeClass.widthSizeClass

        val screenSize =
            when {
                heightClass == WindowHeightSizeClass.Compact && widthClass == WindowWidthSizeClass.Compact -> NowPlayingScreenSize.COMPACT
                heightClass == WindowHeightSizeClass.Compact && widthClass != WindowWidthSizeClass.Compact -> NowPlayingScreenSize.LANDSCAPE
                else -> NowPlayingScreenSize.PORTRAIT
            }

        val paddingModifier =
            remember(screenSize) {
                if (screenSize == NowPlayingScreenSize.LANDSCAPE) {
                    Modifier.padding(16.dp)
                } else {
                    Modifier.padding(start = 16.dp, end = 16.dp, top = 32.dp)
                }
            }

        val playerScreenModifier =
            remember(paddingModifier) {
                Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        alpha = ((progressProvider() - 0.15f) * 2.0f).coerceIn(0.0f, 1.0f)
                    }.then(paddingModifier)
                    .statusBarsPadding()
            }

        AnimatedContent(
            modifier = Modifier.fillMaxSize(),
            targetState = isShowingQueue,
            label = "",
            transitionSpec = {
                if (this.targetState) {
                    fadeIn() togetherWith fadeOut()
                } else {
                    scaleIn(initialScale = 1.2f) + fadeIn() togetherWith fadeOut()
                }
            },
        ) {
            if (it) {
                Text("Queue Screen")
//                QueueScreen(
//                    modifier = Modifier
//                        .fillMaxSize()
//                        .graphicsLayer { alpha = progressProvider() * 2 },
//                    onClose = onCloseQueue,
//                )
            } else {
                ExpandedMusicPlayerContent(
                    playerVM = playerViewModel,
                    modifier = playerScreenModifier.navigationBarsPadding(),
                    openAddToPlaylistDialog = onOpenQueue,
                )
            }
        }
    }
}

@Composable
fun SongControls(
    modifier: Modifier,
    isPlaying: Boolean,
    onPrevious: () -> Unit,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    onJumpForward: () -> Unit,
    onJumpBackward: () -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ControlButton(
            modifier =
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(4.dp)),
            icon = Icons.Rounded.SkipPrevious,
            "Previous", // Changed from "Skip Previous"
            onPrevious,
        )

        Spacer(modifier = Modifier.width(8.dp))

        ControlButton(
            modifier =
                Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(4.dp)),
            icon = Icons.Rounded.FastRewind,
            "Rewind", // Changed from "Jump Back"
            onJumpBackward,
        )

        Spacer(modifier = Modifier.width(16.dp))

        val pausePlayButton =
            remember(isPlaying) {
                if (isPlaying) Icons.Rounded.PauseCircle else Icons.Rounded.PlayCircle
            }

        ControlButton(
            modifier =
                Modifier
                    .size(64.dp)
                    .clip(CircleShape),
            icon = pausePlayButton,
            "Play/Pause", // Changed from "Skip Previous"
            onTogglePlayback,
        )

        Spacer(modifier = Modifier.width(16.dp))

        ControlButton(
            modifier =
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(4.dp)),
            icon = Icons.Rounded.FastForward,
            "Forward", // Changed from "Jump Forward"
            onJumpForward,
        )

        Spacer(modifier = Modifier.width(8.dp))

        ControlButton(
            modifier =
                Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(4.dp)),
            icon = Icons.Rounded.SkipNext,
            "Next", // Changed from "Skip To Next"
            onNext,
        )
    }
}

@Composable
fun ControlButton(
    modifier: Modifier,
    icon: ImageVector,
    contentDescription: String? = null,
    onClick: () -> Unit,
) {
    val iconModifier =
        remember {
            modifier.clickable { onClick() }
        }
    Icon(
        modifier = iconModifier,
        imageVector = icon,
        contentDescription = contentDescription,
    )
}

enum class NowPlayingScreenSize {
    LANDSCAPE,
    PORTRAIT,
    COMPACT,
}
