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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewScreenSizes
import com.sdu.composemusicplayer.navigation.SetupNavigation
import com.sdu.composemusicplayer.presentation.permission.CheckAndRequestPermissions
import com.sdu.composemusicplayer.ui.theme.ComposeMusicPlayerTheme
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val playerVM: PlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val listOfPermissions = mutableListOf<String>().apply {
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
        setContent {
            ComposeMusicPlayerTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CheckAndRequestPermissions(
                        permissions = listOfPermissions
                    ) {
                        SetupNavigation(playerVM = playerVM)
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@PreviewScreenSizes
@Composable
fun GreetingPreview() {
    ComposeMusicPlayerTheme {
        Greeting("Android")
    }
}
