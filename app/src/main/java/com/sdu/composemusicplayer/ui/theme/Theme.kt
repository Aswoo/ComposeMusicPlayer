package com.sdu.composemusicplayer.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.LocalEfficientThumbnailImageLoader
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.LocalInefficientThumbnailImageLoader
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.efficientAlbumArtImageLoader
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.album.inefficientAlbumArtImageLoader

private val DarkColorScheme =
    darkColorScheme(
        primary = Purple200,
        secondary = Purple700,
        tertiary = Teal200,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = Purple500,
        secondary = Purple700,
        tertiary = Teal200,
        /* Other default colors to override
        background = Color.White,
        surface = Color.White,
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.Black,
        onSurface = Color.Black,
         */
    )

@Composable
fun ComposeMusicPlayerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit,
) {

    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }

            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }



    val context = LocalContext.current
    val efficientImageLoader = remember { context.efficientAlbumArtImageLoader() }
    val inefficientImageLoader = remember { context.inefficientAlbumArtImageLoader() }
    CompositionLocalProvider(
        LocalEfficientThumbnailImageLoader provides efficientImageLoader,
        LocalInefficientThumbnailImageLoader provides inefficientImageLoader,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content,
        )
    }
}
