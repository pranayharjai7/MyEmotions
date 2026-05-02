package com.pranayharjai7.myemotions.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Defines a visual theme profile for the app based on mood or default settings.
 */
data class MoodTheme(
    val name: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val gradientColors: List<Color>
)

/**
 * The default Azure Glass theme profile.
 * Consists of a deep Azure blue transitioning into the dark slate background.
 */
val DefaultAzureTheme = MoodTheme(
    name = "Azure Glass",
    primaryColor = AzureBlue,
    backgroundColor = Bg_Dark,
    gradientColors = listOf(AzureBlue, Bg_Dark)
)
