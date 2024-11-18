package com.sdu.composemusicplayer.presentation.music_player_sheet.compoenent

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.data.roomdb.MusicEntity
import com.sdu.composemusicplayer.utils.Constants
import com.sdu.composemusicplayer.utils.move
import com.sdu.composemusicplayer.utils.swap
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun SheetContent(
    isExpaned: Boolean,
    playerVM: PlayerViewModel,
    onBack: () -> Unit
) {
    val musicUiState by playerVM.uiState.collectAsState()
    val musicList = remember {
        mutableStateListOf<MusicEntity>()
    }
    Log.d("HHHI", musicUiState.musicList.toString())

    val reorderableState = rememberReorderableLazyListState(onMove = { from, to ->
        musicList.swap(
            musicList.move(from.index, to.index)
        )
    }, onDragEnd = { from, to ->
            playerVM.onEvent(
                PlayerEvent.UpdateMusicList(
                    musicUiState.musicList.toMutableList().move(from, to)
                )
            )
        })

    LaunchedEffect(key1 = musicUiState.musicList) {
        Log.d("SWAP", "${musicUiState.musicList.size}")
        musicList.swap(musicUiState.musicList)
    }
    BackHandler(isExpaned) {
        onBack()
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(Constants.BOTTOM_SHEET_PEAK_HEIGHT)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.2f)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(color = MaterialTheme.colorScheme.primary)
            )
        }

        LazyColumn(
            state = reorderableState.listState,
            modifier = Modifier
                .reorderable(reorderableState)
                .detectReorderAfterLongPress(reorderableState)
        ) {
            items(
                items = musicUiState.musicList,
                key = { item: MusicEntity -> item.hashCode() }
            ) { music ->
                ReorderableItem(
                    reorderableState = reorderableState,
                    key = music.hashCode()
                ) { isDragging ->
                    val elevation by animateDpAsState(targetValue = if (isDragging) 4.dp else 0.dp)
                    val currentAudioId = musicUiState.currentPlayedMusic.audioId
                    SheetMusicItem(
                        music = music,
                        elevation = elevation,
                        onClick = {},
                        selected = music.audioId == currentAudioId
                    )
                }
            }
        }
    }
}
