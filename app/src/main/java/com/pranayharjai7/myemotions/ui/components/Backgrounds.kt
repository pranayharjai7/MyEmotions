package com.pranayharjai7.myemotions.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.pranayharjai7.myemotions.ui.theme.DefaultAzureTheme
import com.pranayharjai7.myemotions.ui.theme.MoodTheme

/**
 * A consistent, premium background wrapper that implements the Azure Glass aesthetic.
 * Can be easily swapped with different [MoodTheme] profiles in the future.
 */
@Composable
fun AnimatedGradientBackground(
    modifier: Modifier = Modifier,
    theme: MoodTheme = DefaultAzureTheme,
    content: @Composable () -> Unit = {}
) {
    // We animate the colors so future transitions between mood themes are smooth
    val primaryAnimate by animateColorAsState(
        targetValue = theme.primaryColor,
        animationSpec = tween(1500),
        label = "PrimaryColorAnimation"
    )
    val backgroundAnimate by animateColorAsState(
        targetValue = theme.backgroundColor,
        animationSpec = tween(1500),
        label = "BackgroundColorAnimation"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        primaryAnimate.copy(alpha = 0.25f),
                        backgroundAnimate
                    )
                )
            )
    ) {
        content()
    }
}

@Composable
fun FloatingEmotionShapes(modifier: Modifier = Modifier) {
    // Reserved for future particle/ambient effects based on mood
}
