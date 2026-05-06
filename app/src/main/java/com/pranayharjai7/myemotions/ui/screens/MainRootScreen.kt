package com.pranayharjai7.myemotions.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.pranayharjai7.myemotions.ui.navigation.MyEmotionsNavHost
import com.pranayharjai7.myemotions.ui.navigation.Screen

@Composable
fun MainRootScreen(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val isSplash = currentDestination?.hasRoute<Screen.Splash>() == true

    val showBottomBar = when (currentDestination?.route?.split(".")?.lastOrNull()?.split("?")?.firstOrNull()) {
        "Dashboard", "EmotionInsights", "Maps" -> true
        else -> false
    }

    Scaffold(
        bottomBar = {
            AnimatedVisibility(
                visible = showBottomBar,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 8.dp
                ) {
                    val items = listOf(
                        BottomNavItem("Home", Screen.Dashboard, Icons.Default.Home),
                        BottomNavItem("Insights", Screen.EmotionInsights, Icons.Default.Timeline),
                        BottomNavItem("Maps", Screen.Maps, Icons.Default.LocationOn)
                    )

                    items.forEach { item ->
                        val isSelected = currentDestination?.hierarchy?.any { 
                            it.route?.contains(item.screen::class.simpleName ?: "") == true 
                        } == true
                        
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            selected = isSelected,
                            onClick = {
                                navController.navigate(item.screen) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = MaterialTheme.colorScheme.primary,
                                selectedTextColor = MaterialTheme.colorScheme.primary,
                                unselectedIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                unselectedTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }
        },
        containerColor = if (isSplash) Color.White else Color.Transparent
    ) { innerPadding ->
        MyEmotionsNavHost(
            navController = navController,
            innerPadding = innerPadding
        )
    }
}

data class BottomNavItem(
    val label: String,
    val screen: Screen,
    val icon: ImageVector
)
