package com.pranayharjai7.myemotions.ui.navigation

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.pranayharjai7.myemotions.ui.screens.AuthScreen
import com.pranayharjai7.myemotions.ui.screens.DashboardScreen
import com.pranayharjai7.myemotions.ui.screens.SplashScreen
import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Splash : Screen()
    
    @Serializable
    data object Auth : Screen()
    
    @Serializable
    data object Dashboard : Screen()
}

@Composable
fun MyEmotionsNavHost(
    navController: NavHostController,
    viewModel: com.pranayharjai7.myemotions.ui.screens.AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Splash,
        enterTransition = { 
            fadeIn(animationSpec = tween(900, easing = LinearOutSlowInEasing)) + 
            scaleIn(initialScale = 0.8f, animationSpec = tween(900, easing = LinearOutSlowInEasing))
        },
        exitTransition = { 
            fadeOut(animationSpec = tween(700, easing = FastOutLinearInEasing)) + 
            scaleOut(targetScale = 1.2f, animationSpec = tween(700, easing = FastOutLinearInEasing)) 
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(900, easing = LinearOutSlowInEasing)) + 
            scaleIn(initialScale = 1.2f, animationSpec = tween(900, easing = LinearOutSlowInEasing))
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(700, easing = FastOutLinearInEasing)) + 
            scaleOut(targetScale = 0.8f, animationSpec = tween(700, easing = FastOutLinearInEasing))
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
                }
            )
        }
    }
}
