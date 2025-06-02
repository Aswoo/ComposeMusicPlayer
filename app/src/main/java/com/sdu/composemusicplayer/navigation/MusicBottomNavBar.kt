import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.navOptions
import com.sdu.composemusicplayer.navigation.MAIN_NAVIGATION_GRAPH
import com.sdu.composemusicplayer.navigation.PLAYLISTS_NAVIGATION_GRAPH
import com.sdu.composemusicplayer.navigation.Routes
import com.sdu.composemusicplayer.navigation.SETTINGS_NAVIGATION_GRAPH
import com.sdu.composemusicplayer.ui.theme.Gray500
import com.sdu.composemusicplayer.ui.theme.SpotiBlackBar
import com.sdu.composemusicplayer.ui.theme.SpotiGreen
import com.sdu.composemusicplayer.ui.theme.SpotiLightGray

// Spotify-like Bottom Bar
@Composable
fun MusicBottomNavBar(
    modifier: Modifier = Modifier,
    topLevelDestinations: List<Routes>,
    currentDestination: NavDestination?,
    onDestinationSelected: (Routes) -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = SpotiBlackBar,
        tonalElevation = 0.dp,
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
    onDestinationSelected: () -> Unit,
) {
    val icon = if (isSelected) item.iconSelected else item.iconNotSelected

    NavigationBarItem(
        selected = isSelected,
        onClick = onDestinationSelected,
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) SpotiGreen else Gray500,
            )
        },
        label = {
            if (isSelected) {
                Text(
                    text = item.title,
                    color = Color.White,
                )
            }
        },
        alwaysShowLabel = false,
        colors =
            NavigationBarItemDefaults.colors(
                indicatorColor = Color.Transparent,
                selectedIconColor = SpotiGreen,
                unselectedIconColor = SpotiLightGray,
                selectedTextColor = Color.White,
                unselectedTextColor = SpotiLightGray,
            ),
    )
}

fun NavHostController.navigateToTopLevelDestination(routes: Routes) {
    val navOptions =
        navOptions {
            popUpTo(this@navigateToTopLevelDestination.graph.findStartDestination().id) {
                saveState = true
            }
            launchSingleTop = true
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
