@file:OptIn(ExperimentalMaterialApi::class)

package com.sdu.composemusicplayer.presentation.musicPlayerSheet

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sdu.composemusicplayer.data.roomdb.MusicEntity
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent.MotionContent
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent.SheetContent
import com.sdu.composemusicplayer.utils.Constants
import com.sdu.composemusicplayer.utils.move
import com.sdu.composemusicplayer.utils.swap
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
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
            fraction = 0.0f
        )
    }
}

@Composable
fun BottomSheet(
    state: BottomSheetScaffoldState,
    playerVM: PlayerViewModel,
    motionContent: @Composable () -> Unit
) {
    val config = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    val uiState by playerVM.uiState.collectAsState()
    val musicList = remember { mutableStateListOf<MusicEntity>() }

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
//                SheetContent(
//                    isExpaned = state.bottomSheetState.isExpanded,
//                    playerVM = playerVM
//                ) {
//
//                }
                SheetContent(
                    isExpanded = state.bottomSheetState.isExpanded,
                    uiState = uiState,
                    musicList = musicList,
                    onMove = { from, to -> musicList.swap(musicList.move(from, to)) },
                    onDragEnd = { from, to ->
                        playerVM.onEvent(
                            PlayerEvent.UpdateMusicList(
                                uiState.musicList.toMutableList().move(from, to)
                            )
                        )
                    },
                    onBack = {
                        scope.launch {
                            state.bottomSheetState.collapse()
                        }
                    }
                )
            }
        }
    ) {
        motionContent.invoke()
    }
}
