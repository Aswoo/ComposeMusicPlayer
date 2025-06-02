package com.sdu.composemusicplayer.presentation.musicPlayerSheet.util

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.BarState

@OptIn(ExperimentalFoundationApi::class)
fun AnchoredDraggableState<BarState>.update(
    layoutHeightPx: Int,
    barHeightPx: Int,
    bottomBarHeightPx: Int,
): Int {
    var offset = 0
    updateAnchors(
        DraggableAnchors {
            offset =
                (-barHeightPx + layoutHeightPx - bottomBarHeightPx)
            BarState.COLLAPSED at offset.toFloat()
            BarState.EXPANDED at 0.0f
        },
        this.currentValue,
    )
    return offset
}

fun calculateBottomPaddingForContent(
    shouldShowNowPlayingBar: Boolean,
    bottomBarHeight: Dp,
    nowPlayingBarHeight: Dp,
): Dp {
    return bottomBarHeight + (if (shouldShowNowPlayingBar) nowPlayingBarHeight else 0.dp)
}
