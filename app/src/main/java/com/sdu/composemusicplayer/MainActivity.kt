package com.sdu.composemusicplayer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.sdu.composemusicplayer.domain.repository.MediaRepository
import com.sdu.composemusicplayer.navigation.SetupNavigation
import com.sdu.composemusicplayer.presentation.component.LocalCommonMusicAction
import com.sdu.composemusicplayer.presentation.component.rememberCommonMusicActions
import com.sdu.composemusicplayer.presentation.permission.CheckAndRequestPermissions
import com.sdu.composemusicplayer.presentation.player.PlayerViewModel
import com.sdu.composemusicplayer.ui.theme.ComposeMusicPlayerTheme
import com.sdu.composemusicplayer.viewmodel.PlayerEvent
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val playerVM: PlayerViewModel by viewModels()

    @Inject
    lateinit var mediaRepository: MediaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Glance 위젯에서 온 액션 처리
        handleWidgetAction(intent)

        val listOfPermissions =
            mutableListOf<String>().apply {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    add(android.Manifest.permission.READ_MEDIA_AUDIO)
                    add(android.Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    add(android.Manifest.permission.BLUETOOTH_CONNECT)
                } else {
                    add(android.Manifest.permission.BLUETOOTH)
                }
            }
        enableEdgeToEdge()
        val modifier = Modifier.fillMaxSize()
        setContent {
            val navController = rememberNavController()

            ComposeMusicPlayerTheme {
                val commonMusicActions =
                    rememberCommonMusicActions(
                        mediaRepository,
                    )

                CompositionLocalProvider(
                    LocalCommonMusicAction provides commonMusicActions,
                ) {
                    Surface(
                        modifier = modifier,
                        color = MaterialTheme.colorScheme.background,
                    ) {
                        CheckAndRequestPermissions(
                            permissions = listOfPermissions,
                            onPermissionsGranted = { playerVM.onEvent(PlayerEvent.RefreshMusicList) },
                        ) {
                            SetupNavigation(
                                playerVM = playerVM,
                                modifier = modifier,
                                navController = navController,
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * Glance 위젯에서 전달된 액션을 처리합니다.
     */
    private fun handleWidgetAction(intent: Intent?) {
        val action = intent?.getStringExtra("action")
        Log.d("MainActivity", "위젯 액션 수신: $action")
        when (action) {
            "com.sdu.composemusicplayer.action.PLAY_PAUSE" -> {
                Log.d("MainActivity", "재생/일시정지 토글")
                // 현재 재생 상태를 토글
                val currentState = playerVM.uiState.value.isPlaying
                playerVM.onEvent(PlayerEvent.PlayPause(!currentState))
            }
            "com.sdu.composemusicplayer.action.PREVIOUS" -> {
                Log.d("MainActivity", "이전 곡 재생")
                playerVM.onEvent(PlayerEvent.Previous)
            }
            "com.sdu.composemusicplayer.action.NEXT" -> {
                Log.d("MainActivity", "다음 곡 재생")
                playerVM.onEvent(PlayerEvent.Next)
            }
            "com.sdu.composemusicplayer.action.OPEN_APP" -> {
                Log.d("MainActivity", "앱 열기")
                // 이미 앱이 열려있으므로 별도 처리 불필요
            }
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleWidgetAction(intent)
    }
}
