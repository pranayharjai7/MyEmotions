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
    data object EmotionHistory : Screen()

    @Serializable
    data object LogMood : Screen()

    @Serializable
    data object Analytics : Screen()

    @Serializable
    data object Profile : Screen()

    @Serializable
    data class MoodRecommendations(val emotion: String) : Screen()

    @Serializable
    data class EmotionDetails(val recordId: String) : Screen()
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
                    navController.navigate(Screen.EmotionHistory)
                },
                onNavigateToLogMood = {
                    navController.navigate(Screen.LogMood)
                },
                onNavigateToRecommendations = { emotion ->
                    navController.navigate(Screen.MoodRecommendations(emotion))
                }
            )
        }

        composable<Screen.EmotionCapture> {
            com.pranayharjai7.myemotions.ui.screens.emotion.EmotionCaptureScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.EmotionHistory> {
            com.pranayharjai7.myemotions.ui.screens.history.EmotionHistoryScreen(
                onNavigateBack = {
                    navController.popBackStack()
                },
                onNavigateToDetails = { id ->
                    navController.navigate(Screen.EmotionDetails(id))
                }
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

        composable<Screen.Analytics> {
            com.pranayharjai7.myemotions.ui.screens.analytics.AnalyticsScreen()
        }

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
    }
}
