package com.pranayharjai7.myemotions.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.compose.foundation.layout.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.toRoute
import com.pranayharjai7.myemotions.ui.screens.AuthScreen
import com.pranayharjai7.myemotions.ui.screens.AuthViewModel
import com.pranayharjai7.myemotions.ui.screens.DashboardScreen
import com.pranayharjai7.myemotions.ui.screens.ProfileScreen
import com.pranayharjai7.myemotions.ui.screens.SplashScreen
import com.pranayharjai7.myemotions.ui.screens.space.ManageFriendsScreen
import com.pranayharjai7.myemotions.ui.screens.space.SpaceScreen
import kotlinx.serialization.Serializable

/**
 * Navigation destinations for the application.
 */
@Serializable
sealed class Screen {
    @Serializable
    data object Splash : Screen()
    
    @Serializable
    data object Auth : Screen()
    
    @Serializable
    data object Dashboard : Screen()

    @Serializable
    data object EmotionCapture : Screen()

    @Serializable
    data object EmotionInsights : Screen()

    @Serializable
    data object LogMood : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data object Maps : Screen()

    @Serializable
    data object Settings : Screen()

    @Serializable
    data object Reminders : Screen()

    @Serializable
    data object Help : Screen()

    @Serializable
    data object RecommendationsHistory : Screen()

    @Serializable
    data class MoodRecommendations(val emotion: String) : Screen()

    @Serializable
    data class EmotionDetails(val recordId: String) : Screen()

    @Serializable
    data object Space : Screen()

    @Serializable
    data object QrScanner : Screen()
    
    @Serializable
    data object ManageFriends : Screen()

    @Serializable
    data class FriendProfile(val userId: String) : Screen()
}

/**
 * Main navigation host for the application.
 * Defines the navigation graph and transitions.
 */
@Composable
fun MyEmotionsNavHost(
    navController: NavHostController,
    viewModel: AuthViewModel = hiltViewModel(),
    innerPadding: PaddingValues = PaddingValues(0.dp)
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
        modifier = Modifier.padding(innerPadding),
        enterTransition = { 
            fadeIn(animationSpec = tween(700, easing = LinearOutSlowInEasing)) + 
            slideInHorizontally(initialOffsetX = { 300 }, animationSpec = tween(700, easing = LinearOutSlowInEasing))
        },
        exitTransition = { 
            fadeOut(animationSpec = tween(500, easing = FastOutLinearInEasing)) + 
            slideOutHorizontally(targetOffsetX = { -300 }, animationSpec = tween(500, easing = FastOutLinearInEasing))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(700, easing = LinearOutSlowInEasing)) + 
            slideInHorizontally(initialOffsetX = { -300 }, animationSpec = tween(700, easing = LinearOutSlowInEasing))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(500, easing = FastOutLinearInEasing)) + 
            slideOutHorizontally(targetOffsetX = { 300 }, animationSpec = tween(500, easing = FastOutLinearInEasing))
        }
    ) {
        composable<Screen.Splash> {
            SplashScreen(
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                },
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.Splash) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screen.Auth> {
            AuthScreen(
                onNavigateToDashboard = {
                    navController.navigate(Screen.Dashboard) {
                        popUpTo(Screen.Auth) { inclusive = true }
                    }
                }
            )
        }
        
        composable<Screen.Dashboard> {
            DashboardScreen(
                onLogout = {
                    viewModel.signOut()
                    navController.navigate(Screen.Auth) {
                        popUpTo(Screen.Dashboard) { inclusive = true }
                    }
                },
                onNavigateToHistory = {
                    navController.navigate(Screen.EmotionInsights)
                },
                onNavigateToLogMood = {
                    navController.navigate(Screen.LogMood)
                },
                onNavigateToRecommendations = { emotion ->
                    navController.navigate(Screen.MoodRecommendations(emotion))
                },
                onNavigateToProfile = { navController.navigate(Screen.Profile) },
                onNavigateToAnalytics = { navController.navigate(Screen.EmotionInsights) },
                onNavigateToSettings = { navController.navigate(Screen.Settings) },
                onNavigateToReminders = { navController.navigate(Screen.Reminders) },
                onNavigateToHelp = { navController.navigate(Screen.Help) },
                onNavigateToRecommendationsHistory = { navController.navigate(Screen.RecommendationsHistory) },
                onNavigateToManageFriends = { navController.navigate(Screen.ManageFriends) }
            )
        }

        composable<Screen.EmotionCapture> {
            com.pranayharjai7.myemotions.ui.screens.emotion.EmotionCaptureScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.EmotionInsights> {
            com.pranayharjai7.myemotions.ui.screens.insights.EmotionInsightsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToDetails = { recordId -> navController.navigate(Screen.EmotionDetails(recordId)) }
            )
        }

        composable<Screen.LogMood> {
            com.pranayharjai7.myemotions.ui.screens.logging.LogMoodScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToScan = {
                    navController.navigate(Screen.EmotionCapture)
                }
            )
        }

        // Analytics route removed and merged into EmotionInsights

        composable<Screen.Profile> {
            ProfileScreen(
                onLogout = {
                    viewModel.signOut()
                    navController.navigate(Screen.Auth) {
                        popUpTo(Screen.Dashboard) { inclusive = true }
                    }
                }
            )
        }

        composable<Screen.MoodRecommendations> { backStackEntry ->
            val route: Screen.MoodRecommendations = backStackEntry.toRoute()
            com.pranayharjai7.myemotions.ui.screens.emotion.MoodRecommendationsScreen(
                emotion = route.emotion,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.EmotionDetails> { backStackEntry ->
            val route: Screen.EmotionDetails = backStackEntry.toRoute()
            com.pranayharjai7.myemotions.ui.screens.history.EmotionDetailsScreen(
                recordId = route.recordId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Maps> {
            com.pranayharjai7.myemotions.ui.screens.space.SpaceMapScreen(
                onNavigateToProfile = { userId -> navController.navigate(Screen.FriendProfile(userId)) }
            )
        }

        composable<Screen.Space>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None },
            deepLinks = listOf(
                androidx.navigation.navDeepLink<Screen.Space>(basePath = "myemotions://addfriend")
            )
        ) {
            SpaceScreen(
                onNavigateToProfile = { userId -> navController.navigate(Screen.FriendProfile(userId)) }
            )
        }

        composable<Screen.Settings> {
            com.pranayharjai7.myemotions.ui.screens.SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Reminders> {
            com.pranayharjai7.myemotions.ui.screens.RemindersScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.Help> {
            com.pranayharjai7.myemotions.ui.screens.HelpScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.RecommendationsHistory> {
            com.pranayharjai7.myemotions.ui.screens.RecommendationsHistoryScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }



        composable<Screen.QrScanner> {
            com.pranayharjai7.myemotions.ui.screens.space.QrScannerScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.FriendProfile> {
            val args = it.toRoute<Screen.FriendProfile>()
            com.pranayharjai7.myemotions.ui.screens.space.FriendProfileScreen(
                userId = args.userId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Screen.ManageFriends> {
            ManageFriendsScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToQrScanner = { navController.navigate(Screen.QrScanner) },
                onNavigateToProfile = { userId -> navController.navigate(Screen.FriendProfile(userId)) }
            )
        }
    }
}
