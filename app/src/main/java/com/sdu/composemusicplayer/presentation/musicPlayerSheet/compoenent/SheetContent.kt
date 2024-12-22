package com.sdu.composemusicplayer.presentation.musicPlayerSheet.compoenent

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import androidx.compose.ui.unit.dp
import com.sdu.composemusicplayer.data.roomdb.MusicEntity
import com.sdu.composemusicplayer.utils.Constants
import com.sdu.composemusicplayer.utils.move
import com.sdu.composemusicplayer.utils.swap
import com.sdu.composemusicplayer.viewmodel.MusicUiState
import org.burnoutcrew.reorderable.ReorderableItem
import org.burnoutcrew.reorderable.detectReorderAfterLongPress
import org.burnoutcrew.reorderable.rememberReorderableLazyListState
import org.burnoutcrew.reorderable.reorderable

@Composable
fun SheetContent(
    isExpanded: Boolean,
    uiState: MusicUiState,
    musicList: MutableList<MusicEntity>,
    onMove: (Int, Int) -> Unit,
    onDragEnd: (Int, Int) -> Unit,
    onBack: () -> Unit
) {
    val reorderableState = rememberReorderableLazyListState(onMove = { from, to ->
        onMove(from.index, to.index)
    }, onDragEnd = { from, to ->
            onDragEnd(from, to)
        })

    BackHandler(isExpanded) {
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
                items = uiState.musicList,
                key = { item: MusicEntity -> item.hashCode() }
            ) { music ->
                ReorderableItem(
                    reorderableState = reorderableState,
                    key = music.hashCode()
                ) { isDragging ->
                    val elevation by animateDpAsState(targetValue = if (isDragging) 4.dp else 0.dp)
                    val currentAudioId = uiState.currentPlayedMusic.audioId
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

@PreviewScreenSizes
@Composable
fun PreviewSheetContent() {
    val sampleUiState = MusicUiState(
        musicList = listOf(
            MusicEntity(
                audioId = 1L,
                title = "Test Song",
                artist = "Test Artist",
                duration = 180000L,
                albumPath = "/path/to/album",
                audioPath = "/path/to/test/song.mp3"
            ),
            MusicEntity(
                audioId = 2L,
                title = "Test Song",
                artist = "Test Artist",
                duration = 180000L,
                albumPath = "/path/to/album",
                audioPath = "/path/to/test/song.mp3"
            ),
            MusicEntity(
                audioId = 3L,
                title = "Test Song",
                artist = "Test Artist",
                duration = 180000L,
                albumPath = "/path/to/album",
                audioPath = "/path/to/test/song.mp3"
            )
        ),
        currentPlayedMusic = MusicEntity(
            audioId = 1L,
            title = "Test Song",
            artist = "Test Artist",
            duration = 180000L,
            albumPath = "/path/to/album",
            audioPath = "/path/to/test/song.mp3"
        )
    )
    val musicList = remember {
        mutableStateListOf<MusicEntity>().apply {
            addAll(
                sampleUiState.musicList
            )
        }
    }

    SheetContent(
        isExpanded = true,
        uiState = sampleUiState,
        musicList = musicList,
        onMove = { from, to -> musicList.swap(musicList.move(from, to)) },
        onDragEnd = { _, _ -> /* No-op in preview */ },
        onBack = { /* No-op in preview */ }
    )
}
