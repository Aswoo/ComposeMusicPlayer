package com.sdu.composemusicplayer.presentation.musicPlayerSheet

import MusicBottomNavBar
import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import com.sdu.composemusicplayer.MusicAppState
import com.sdu.composemusicplayer.navigation.Routes
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.util.CompactScreenUiStateConfig
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.util.calculateBottomPaddingForContent
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.util.rememberCompactScreenUiState
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.util.update
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val COMPACT_NOW_PLAYING_BAR_HEIGHT = 56.dp
private const val ANIMATION_DURATION = 600
private const val INITIAL_OFFSET_Y_MULTIPLIER = 2

@OptIn(ExperimentalFoundationApi::class)
@Composable
@Suppress("LongParameterList", "LongMethod")
fun CompactAppScaffold(
    appState: MusicAppState,
    modifier: Modifier,
    playerScreenAnchors: AnchoredDraggableState<BarState>,
    topLevelDestinations: List<Routes>,
    currentDestination: NavDestination?,
    onDestinationSelected: (Routes) -> Unit,
    content: @Composable (Modifier, MutableState<Modifier>) -> Unit,
) {
    val density = LocalDensity.current
    val shouldShowPlayerBar by appState.shouldShowPlayerScreen.collectAsState(initial = false)
    val playerBarHeightPx = with(density) { COMPACT_NOW_PLAYING_BAR_HEIGHT.toPx() }
//    val shouldShowBottomBar by appState.shouldShowBottomBar.collectAsState(initial = false)

    var layoutHeightPx = remember { 0 }
    val bottomNavBarHeightPx =
        with(density) { 80.dp.toPx() }

    var playerBarMinOffset by remember {
        mutableIntStateOf(0)
    }

    val scrollProvider = { 1 - (appState.playerScreenOffset() / playerBarMinOffset) }

    val contentModifier = remember { mutableStateOf<Modifier>(Modifier) }

    LaunchedEffect(key1 = shouldShowPlayerBar) {
        Log.d("OUT", "shouldShowPlayerBar :  $shouldShowPlayerBar")
        if (!shouldShowPlayerBar) {
            Log.d("IN", "shouldShowPlayerBar :  $shouldShowPlayerBar")
            playerScreenAnchors.animateTo(BarState.COLLAPSED)
        }
    }

    LaunchedEffect(key1 = shouldShowPlayerBar) {
        contentModifier.value =
            Modifier.padding(
                bottom =
                    calculateBottomPaddingForContent(
                        shouldShowPlayerBar,
                        // Use actual bottom nav bar height
                        with(density) { bottomNavBarHeightPx.toDp() },
                        COMPACT_NOW_PLAYING_BAR_HEIGHT,
                    ),
            )
    }

    val uiState =
        rememberCompactScreenUiState(
            config =
                CompactScreenUiStateConfig(
                    screenHeightPx = layoutHeightPx,
                    playerAnchors = playerScreenAnchors,
                    scrollProvider = scrollProvider,
                    bottomBarHeightPx = bottomNavBarHeightPx.toInt(),
                    density = density,
                    isPinnedMode = false,
                    isPlayerVisible = shouldShowPlayerBar,
                ),
        )

    // App itself
    Box(modifier = modifier) {
        // DrawContentFirst
        Box(modifier = Modifier.fillMaxSize()) {
            content(
                Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxSize()
                    .navigationBarsPadding(),
                contentModifier,
            )
        }

        AnimatedVisibility(
            visible = shouldShowPlayerBar,
            enter =
                slideInVertically(
                    tween(ANIMATION_DURATION),
                    initialOffsetY = { playerBarHeightPx.roundToInt() * INITIAL_OFFSET_Y_MULTIPLIER },
                ),
            exit =
                slideOutVertically(
                    tween(ANIMATION_DURATION),
                    targetOffsetY = { -playerBarHeightPx.roundToInt() },
                ),
        ) {
            AnimatedVisibility(
                visible = shouldShowPlayerBar,
                enter =
                    slideInVertically(
                        tween(ANIMATION_DURATION),
                        initialOffsetY = { playerBarHeightPx.roundToInt() * INITIAL_OFFSET_Y_MULTIPLIER },
                    ),
                exit =
                    slideOutVertically(
                        tween(ANIMATION_DURATION),
                        targetOffsetY = { -playerBarHeightPx.roundToInt() },
                    ),
            ) {
                PlayerScreen(
                    barHeight = COMPACT_NOW_PLAYING_BAR_HEIGHT,
                    nowPlayingBarPadding = PaddingValues(0.dp),
                    modifier =
                        Modifier
                            .fillMaxSize()
                            .offset {
                                uiState.getNowPlayingOffset()
                            }.onSizeChanged { layoutSize ->
                                layoutHeightPx = layoutSize.height
                                playerBarMinOffset =
                                    playerScreenAnchors
                                        .update(
                                            layoutHeightPx,
                                            playerBarHeightPx.toInt(),
                                            bottomNavBarHeightPx.toInt(),
                                        )
                            }.anchoredDraggable(playerScreenAnchors, Orientation.Vertical)
                            .pointerInput(Unit) {
                                // PlayerScreen에서 제스처 차단
                                detectHorizontalDragGestures { _, _ ->
                                    // 좌우 스와이프 이벤트를 차단
                                }
                            },
                    onCollapseNowPlaying = {
                        appState.coroutineScope.launch {
                            playerScreenAnchors.animateTo(BarState.COLLAPSED)
                        }
                    },
                    onExpandNowPlaying = {
                        appState.coroutineScope.launch {
                            playerScreenAnchors.animateTo(BarState.EXPANDED)
                        }
                    },
                    isExpanded = playerScreenAnchors.currentValue == BarState.EXPANDED,
                    progressProvider = scrollProvider,
                    viewModel = appState.playerViewModel,
                )
            }
        }

        MusicBottomNavBar(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .graphicsLayer { alpha = uiState.bottomBarAlpha }
                    .offset {
                        uiState.getBottomBarOffset()
                    },
            topLevelDestinations = topLevelDestinations,
            currentDestination = currentDestination,
            onDestinationSelected = onDestinationSelected,
        )
    }
}
