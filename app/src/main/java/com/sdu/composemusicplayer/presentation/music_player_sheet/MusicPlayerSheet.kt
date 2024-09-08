@file:OptIn(ExperimentalMaterialApi::class)

package com.sdu.composemusicplayer.presentation.music_player_sheet

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomSheetScaffold
import androidx.compose.material.BottomSheetScaffoldState
import androidx.compose.material.BottomSheetValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.rememberBottomSheetScaffoldState
import androidx.compose.material.rememberBottomSheetState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sdu.composemusicplayer.presentation.music_player_sheet.compoenent.MotionContent
import com.sdu.composemusicplayer.presentation.music_player_sheet.compoenent.SheetContent
import com.sdu.composemusicplayer.utils.Constants
import com.sdu.composemusicplayer.utils.currentfraction
import com.sdu.composemusicplayer.utils.currentfraction3
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MusicPlayerSheet(navController: NavController, playerVM: PlayerViewModel) {
    val musicUiState by playerVM.uiState.collectAsState()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberBottomSheetScaffoldState(
        bottomSheetState = rememberBottomSheetState(
           initialValue = BottomSheetValue.Collapsed
        )
    )
    BackHandler {
        when {
            scaffoldState.bottomSheetState.isExpanded -> scope.launch {
                scaffoldState.bottomSheetState.collapse()
            }
            else -> navController.popBackStack()
        }
    }
    BottomSheet(
        state = scaffoldState,
        playerVM = playerVM
    ) {
        MotionContent(
            playerVM = playerVM,
            fraction = scaffoldState.currentfraction
        )
    }
}

@Composable
fun BottomSheet(
    state: BottomSheetScaffoldState,
    playerVM: PlayerViewModel, motionContent: @Composable () -> Unit
) {
    val config = LocalConfiguration.current
    val scope = rememberCoroutineScope()

    BottomSheetScaffold(
        scaffoldState = state,
        sheetPeekHeight = Constants.BOTTOM_SHEET_PEAK_HEIGHT,
        sheetContent = {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .fillMaxWidth()
                    .fillMaxHeight(
                        0.99f.minus(Constants.TOP_MUSIC_PLAYER_HEIGHT.value / config.screenHeightDp)
                    )
            ) {
                SheetContent(
                    isExpaned = state.bottomSheetState.isExpanded,
                    playerVM = playerVM
                ) {
                    scope.launch {
                        state.bottomSheetState.collapse()
                    }
                }

            }
        }) {
        motionContent.invoke()

    }

}