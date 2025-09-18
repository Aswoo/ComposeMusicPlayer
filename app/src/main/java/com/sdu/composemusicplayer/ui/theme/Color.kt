package com.sdu.composemusicplayer.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sdu.composemusicplayer.utils.AndroidConstants

// Material Design 색상들
private const val PURPLE_200_HEX = 0xFFBB86FC
private const val PURPLE_500_HEX = 0xFF6200EE
private const val PURPLE_700_HEX = 0xFF3700B3
private const val TEAL_200_HEX = 0xFF03DAC5
private const val GRAY_200_HEX = 0xEEEEEE
private const val GRAY_500_HEX = 0xFF9E9E9E
private const val GRAY_600_HEX = 0xFF757575
private const val GRAY_700_HEX = 0xFF616161

val Purple200 = Color(PURPLE_200_HEX)
val Purple500 = Color(PURPLE_500_HEX)
val Purple700 = Color(PURPLE_700_HEX)
val Teal200 = Color(TEAL_200_HEX)
val Gray200 = Color(GRAY_200_HEX)
val Gray500 = Color(GRAY_500_HEX)
val Gray600 = Color(GRAY_600_HEX)
val Gray700 = Color(GRAY_700_HEX)

// Spotify 테마 색상들
val SpotiGreen = Color(AndroidConstants.Color.SPOTIFY_GREEN)
val SpotiBlack = Color(GRAY_700_HEX)
val SpotiBlackBar = Color(AndroidConstants.Color.SPOTIFY_BLACK)
val SpotiBackground = Color(AndroidConstants.Color.SPOTIFY_BLACK)
val SpotiWhite = Color(AndroidConstants.Color.SPOTIFY_WHITE)
val SpotiDarkGray = Color(AndroidConstants.Color.SPOTIFY_DARK_GRAY)
val SpotiGray = Color(AndroidConstants.Color.SPOTIFY_GRAY)
val SpotiDivider = Color(AndroidConstants.Color.SPOTIFY_DIVIDER)
val SpotiLightGray = Color(AndroidConstants.Color.SPOTIFY_LIGHT_GRAY)

val LightRed = Color(AndroidConstants.Color.LIGHT_RED)

val TextDefaultColor: Color
    @Composable get() = if (!isSystemInDarkTheme()) {
        Color(AndroidConstants.Color.TEXT_DEFAULT_LIGHT)
    } else {
        Color(AndroidConstants.Color.TEXT_DEFAULT_DARK)
    }

val TintDefaultColor: Color
    @Composable get() = if (!isSystemInDarkTheme()) {
        Color(AndroidConstants.Color.TEXT_DEFAULT_LIGHT)
    } else {
        Color(AndroidConstants.Color.TEXT_DEFAULT_DARK)
    }
