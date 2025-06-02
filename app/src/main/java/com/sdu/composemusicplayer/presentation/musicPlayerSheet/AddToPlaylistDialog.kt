package com.sdu.composemusicplayer.presentation.musicPlayerSheet

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckBoxOutlineBlank
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.sdu.composemusicplayer.domain.model.PlaylistInfo
import com.sdu.composemusicplayer.presentation.component.dialog.AddToPlaylistState
import com.sdu.composemusicplayer.presentation.component.dialog.AddToPlaylistViewModel
import com.sdu.composemusicplayer.presentation.component.dialog.rememberCreatePlaylistDialog
import com.sdu.composemusicplayer.ui.theme.SpotiGreen

@Composable
fun AddToPlaylistDialog(
    musicUri: String,
    onDismissRequest: () -> Unit,
    viewModel: AddToPlaylistViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val selectedPlaylistId by viewModel.selectedPlaylistId.collectAsState()
    val createPlaylistsDialog = rememberCreatePlaylistDialog()

    val playlists =
        remember(state) {
            when (state) {
                is AddToPlaylistState.Success -> {
                    (state as AddToPlaylistState.Success).playlists
                }

                else -> mutableStateListOf()
            }
        }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(Color.Black),
            color = Color.Black,
        ) {
            AddToPlaylistContent(
                playlists = playlists,
                selectedPlaylistId = selectedPlaylistId,
                onPlaylistSelect = { id -> viewModel.selectPlayList(id) },
                onCreateNewPlaylist = {
                    createPlaylistsDialog.launch()
                },
                onClearAll = viewModel::clearAllPlaylists,
                onNavigateBack = {
                    onDismissRequest()
                },
                onComplete = {
                    viewModel.onComplete(musicUri)
                    onDismissRequest()
                },
            )
        }
    }
}

@Composable
fun AddToPlaylistContent(
    playlists: List<PlaylistInfo>,
    selectedPlaylistId: Int?,
    onPlaylistSelect: (Int) -> Unit,
    onCreateNewPlaylist: () -> Unit,
    onNavigateBack: () -> Unit,
    onClearAll: () -> Unit,
    onComplete: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text("플레이리스트에 추가하기", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            TextButton(onClick = onClearAll) {
                Text("모두 지우기", color = SpotiGreen)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onCreateNewPlaylist,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            shape = MaterialTheme.shapes.large,
        ) {
            Text("새 플레이리스트", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("저장된 플레이리스트", color = Color.White, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.weight(1f)) {
            items(playlists) { playlist ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onPlaylistSelect(playlist.id) }
                            .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier =
                                Modifier
                                    .size(40.dp)
                                    .background(Color.Gray),
                        ) // replace with Image if needed
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(playlist.name, color = Color.White, fontSize = 16.sp)
                            val numberOfMusicText =
                                if (playlist.numberOfMusic != 0) {
                                    "${playlist.numberOfMusic} 곡"
                                } else {
                                    "비어 있음"
                                }
                            Text(numberOfMusicText, color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                    if (playlist.id == selectedPlaylistId) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Green)
                    } else {
                        Icon(Icons.Default.CheckBoxOutlineBlank, contentDescription = null, tint = Color.Green)
                    }
                }
            }
        }

        Button(
            onClick = onComplete,
            modifier =
                Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp)
                    .height(48.dp)
                    .fillMaxWidth(0.4f),
            shape = MaterialTheme.shapes.large,
            colors = ButtonDefaults.buttonColors(containerColor = SpotiGreen),
        ) {
            Text("완료", color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}
