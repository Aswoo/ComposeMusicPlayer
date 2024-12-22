@file:OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterialNavigationApi::class,
    ExperimentalMaterialApi::class
)

package com.sdu.composemusicplayer.navigation

import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.SwipeableDefaults
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.google.accompanist.navigation.material.bottomSheet
import com.sdu.composemusicplayer.presentation.mainScreen.MainScreen
import com.sdu.composemusicplayer.presentation.musicPlayerSheet.MusicPlayerSheet
import com.sdu.composemusicplayer.viewmodel.PlayerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetupNavigation(playerVM: PlayerViewModel, modifier: Modifier = Modifier) {
    val sheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        animationSpec = SwipeableDefaults.AnimationSpec,
        skipHalfExpanded = true
    )

    val bottomSheetNavigator = remember(sheetState) {
        BottomSheetNavigator(sheetState = sheetState)
    }
    val navController = rememberNavController(bottomSheetNavigator)

    ModalBottomSheetLayout(
        bottomSheetNavigator = bottomSheetNavigator,
        sheetBackgroundColor = Color.Transparent,
        sheetShape = MaterialTheme.shapes.large.copy(
            bottomStart = CornerSize(0.dp),
            bottomEnd = CornerSize(0.dp)
        ),
        modifier = modifier
    ) {
        NavHost(
            navController = navController,
            startDestination = Routes.Main.name
        ) {
            composable(Routes.Main.name) {
                MainScreen(
                    navController = navController,
                    playerVM = playerVM
                )
            }
            bottomSheet(Routes.Player.name) {
                MusicPlayerSheet(navController = navController, playerVM = playerVM)
            }
        }
    }
//
//    val sheetState = rememberModalBottomSheetState(
//        skipPartiallyExpanded = true
//    )
//
//    val navController = rememberNavController()
//
//    ModalBottomSheet(
//        onDismissRequest = { },
//        sheetState = sheetState,
//        containerColor = Color.Transparent,
//        shape = MaterialTheme.shapes.large.copy(
//            bottomStart = CornerSize(0.dp),
//            bottomEnd = CornerSize(0.dp)
//        )
//    ) {
//        NavHost(
//            navController = navController,
//            startDestination = Routes.Main.name
//        ) {
//            composable(Routes.Main.name) {
//                MainScreen(
//                    navController = navController,
//                    playerVM = playerVM
//                )
//            }
//            bottomSheet(Routes.Player.name) {
//                MusicPlayerSheet(navController = navController, playerVM = playerVM)
//            }
//        }
//    }

//    ModalBottomSheetLayout(
//        bottomSheetNavigator = ,
//        sheetBackgroundColor = Color.Transparent,
//        sheetShape = MaterialTheme.shapes.large.copy(
//            bottomEnd = CornerSize(0),
//            bottomStart = CornerSize(0)
//        ),
//        modifier = modifier
//    ) {
//
//
//    }
}
