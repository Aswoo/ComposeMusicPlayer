@file:OptIn(ExperimentalFoundationApi::class)

package com.sdu.composemusicplayer.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.movableContentOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.sdu.composemusicplayer.presentation.mainScreen.MainScreen
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.CompactAppScaffold
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.BarState
import com.sdu.composemusicplayer.presentation.playlists.playlist.PlaylistsScreen
import com.sdu.composemusicplayer.presentation.playlists.playlistdetail.PlaylistDetailScreen
import com.sdu.composemusicplayer.presentation.setting.Setting
import com.sdu.composemusicplayer.rememberMusicAppState
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel
import navigateToTopLevelDestination


val topLevelDestinations =
    listOf(
        Routes.Main,
        Routes.PLAYLISTS,
        Routes.SETTINGS,
    )

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SetupNavigation(
    playerVM: PlayerViewModel,
    modifier: Modifier = Modifier,
    navController: NavHostController,
) {

    val density = LocalDensity.current
    val decaySpec = rememberSplineBasedDecay<Float>()
    val playerScreenAnchors = remember {
        AnchoredDraggableState(
            initialValue = BarState.COLLAPSED,
            anchors = DraggableAnchors {
                BarState.COLLAPSED at 0f
                BarState.EXPANDED at 1f
            },
            positionalThreshold = { distance: Float -> 0.5f * distance },
            velocityThreshold = { with(density) { 70.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decaySpec,
        )
    }

    val appState = rememberMusicAppState(
        playerViewModel = playerVM,
        playerScreenOffset = {
            if (playerScreenAnchors.anchors.size > 0)
                playerScreenAnchors.requireOffset()
            else 0.0f
        },
        navHostController = navController,
    )
    val navHost = remember {
        movableContentOf<Modifier, MutableState<Modifier>> { navHostModifier, contentModifier ->
            NavHost(
                modifier = navHostModifier,
                navController = appState.navHostController,
                startDestination = Routes.Main.route,
            ) {
                composable(Routes.Main.route) {
                    MainScreen(
                        navController = navController,
                        playerVM = playerVM,
                    )
                }
                composable(Routes.PLAYLISTS.route) {
                    PlaylistsScreen(
                        modifier = Modifier,
                        onNavigateToPlaylist = { playlistId ->
                            navController.navigate("playlist_detail/$playlistId")
                        },
                    )
                }
                composable(Routes.SETTINGS.route) {
                    Setting()
                }
                composable("playlist_detail/{id}") { backStackEntry ->
                    PlaylistDetailScreen(modifier = Modifier, onBackPressed = { navController.popBackStack() })
                }
            }
        }
    }
    CompactAppScaffold(
        appState = appState,
        modifier = modifier,
        playerScreenAnchors = playerScreenAnchors,
        content = navHost,
        topLevelDestinations = topLevelDestinations,
        currentDestination = navController.currentBackStackEntryAsState().value?.destination,
        onDestinationSelected = { navController.navigateToTopLevelDestination(it) },
    )
}
