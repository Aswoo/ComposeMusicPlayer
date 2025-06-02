package com.sdu.composemusicplayer

import android.os.Build
import android.os.Bundle
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
import com.sdu.composemusicplayer.core.media.MediaRepository
import com.sdu.composemusicplayer.navigation.SetupNavigation
import com.sdu.composemusicplayer.presentation.component.LocalCommonMusicAction
import com.sdu.composemusicplayer.presentation.component.rememberCommonMusicActions
import com.sdu.composemusicplayer.presentation.permission.CheckAndRequestPermissions
import com.sdu.composemusicplayer.ui.theme.ComposeMusicPlayerTheme
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val playerVM: PlayerViewModel by viewModels()

    @Inject
    lateinit var mediaRepository: MediaRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
}
