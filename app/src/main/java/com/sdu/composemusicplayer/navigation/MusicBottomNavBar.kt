package com.omar.musica.navigation

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.sdu.composemusicplayer.navigation.MAIN_NAVIGATION_GRAPH
import com.sdu.composemusicplayer.navigation.PLAYLISTS_NAVIGATION_GRAPH
import com.sdu.composemusicplayer.navigation.Routes
import com.sdu.composemusicplayer.navigation.SETTINGS_NAVIGATION_GRAPH


@Composable
fun MusicBottomNavBar(
    modifier: Modifier,
    topLevelDestinations: List<Routes>,
    currentDestination: NavDestination?,
    onDestinationSelected: (Routes) -> Unit
) {

    NavigationBar(
        modifier = modifier
    ) {
        topLevelDestinations.forEach { item ->
            val isSelected = currentDestination.isTopLevelDestinationInHierarchy(item)
            BottomNavItem(item = item, isSelected = isSelected) {
                onDestinationSelected(item)
            }
        }
    }

}

@Composable
fun RowScope.BottomNavItem(
    item: Routes,
    isSelected: Boolean,
    onDestinationSelected: () -> Unit
) {

    val icon = if (isSelected) item.iconSelected else item.iconNotSelected
    NavigationBarItem(
        selected = isSelected,
        onClick = onDestinationSelected,
        icon = { Icon(imageVector = icon, contentDescription = null) },
        label = { Text(text = item.title) },
        alwaysShowLabel = false
    )

}


fun NavHostController.navigateToTopLevelDestination(routes: Routes) {
    val navOptions = navOptions {
        popUpTo(this@navigateToTopLevelDestination.graph.findStartDestination().id) {
            saveState = true
        }
        // Avoid multiple copies of the same destination when
        // reselecting the same item
        launchSingleTop = true
        // Restore state when reselecting a previously selected item
        restoreState = true
    }

    when (routes) {
        Routes.Main -> navigate(MAIN_NAVIGATION_GRAPH, navOptions)
        Routes.PLAYLISTS -> navigate(PLAYLISTS_NAVIGATION_GRAPH, navOptions)
        Routes.SETTINGS -> navigate(SETTINGS_NAVIGATION_GRAPH, navOptions)
    }
}

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: Routes) =
    this?.hierarchy?.any {
        it.route?.contains(destination.route, true) ?: false
    } ?: false
