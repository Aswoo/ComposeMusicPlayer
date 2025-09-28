@file:OptIn(ExperimentalFoundationApi::class)

package com.sdu.composemusicplayer.presentation.musicPlayerSheet.util

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.BarState

private const val ANIMATION_DURATION = 400

/**
 * This class is responsible to calculate the offsets
 * of the components (bottom bar, NowPlayingScreen) in the Compact App Screen.
 * It assumes that everything is initially placed into its normal position
 * (ie. the bottom bar is shown and the NowPlayingScreen is above it)
 */
class CompactAppOffsetCalculator
    @OptIn(ExperimentalFoundationApi::class)
    constructor(
        private val config: CompactAppOffsetCalculatorConfig,
    ) {
        /**
         * Returns the offset of the NowPlaying screen
         * taking into consideration the drag commands of the user
         * and the visibility of the bottomBar
         */
        @OptIn(ExperimentalFoundationApi::class)
        fun getNowPlayingOffset(): IntOffset {
            val navigationInsetPx = getBottomNavInsetsPx()

            val dragProgress = config.scrollProvider()
            val dragOffset = config.playerAnchors.requireOffset().toInt()

            val baseOffset = dragOffset - navigationInsetPx * (1 - dragProgress)
            val bottomBarOffset =
                (getBottomBarOffset().y)
                    .coerceIn(0, config.bottomBarHeightPx) * (1 - dragProgress)

            val y = baseOffset.toInt() + bottomBarOffset.toInt()
            return IntOffset(0, y)
        }

        /**
         * Returns the bottom bar offset when it slides out and into
         * the screen
         */
        fun getBottomBarOffset(): IntOffset {
            val navigationInsetsPx = getBottomNavInsetsPx()
            val totalBarHeightPx = config.bottomBarHeightPx + navigationInsetsPx

            val nowPlayingExpansionProgress = config.scrollProvider().coerceIn(0.0f, 1.0f)
            var y: Float = config.bottomBarOffsetAnimation.value.toFloat()

            if (config.isPlayerVisible) {
                y += totalBarHeightPx * nowPlayingExpansionProgress
            }

            return IntOffset(0, y.toInt())
        }

        private fun getBottomNavInsetsPx() = config.navigationInsets.getBottom(config.density)

        val bottomBarAlpha: Float
            get() = 1 - config.scrollProvider()
    }

@OptIn(ExperimentalFoundationApi::class)
data class CompactAppOffsetCalculatorConfig(
    val playerAnchors: AnchoredDraggableState<BarState>,
    val bottomBarOffsetAnimation: State<Int>,
    val scrollProvider: () -> Float,
    val navigationInsets: WindowInsets,
    val bottomBarHeightPx: Int,
    val density: Density,
    val isPlayerVisible: Boolean,
    val isPinnedMode: Boolean,
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun rememberCompactScreenUiState(config: CompactScreenUiStateConfig): CompactAppOffsetCalculator {
    val navigationInsets = WindowInsets.navigationBars

    val bottomNavBarOffsetPx =
        animateIntAsState(
            targetValue = 0,
            animationSpec = tween(ANIMATION_DURATION),
            label = "",
        )

    return remember(
        config.screenHeightPx,
        config.scrollProvider,
        navigationInsets,
        config.bottomBarHeightPx,
        config.density,
        config.isPinnedMode,
        config.isPlayerVisible,
    ) {
        CompactAppOffsetCalculator(
            CompactAppOffsetCalculatorConfig(
                playerAnchors = config.playerAnchors,
                bottomBarOffsetAnimation = bottomNavBarOffsetPx,
                scrollProvider = config.scrollProvider,
                navigationInsets = navigationInsets,
                bottomBarHeightPx = config.bottomBarHeightPx,
                density = config.density,
                isPlayerVisible = config.isPlayerVisible,
                isPinnedMode = config.isPinnedMode,
            ),
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
data class CompactScreenUiStateConfig(
    val screenHeightPx: Int,
    val playerAnchors: AnchoredDraggableState<BarState>,
    val scrollProvider: () -> Float,
    val bottomBarHeightPx: Int,
    val density: Density,
    val isPinnedMode: Boolean,
    val isPlayerVisible: Boolean,
)
