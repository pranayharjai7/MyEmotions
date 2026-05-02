package com.pranayharjai7.myemotions.ui.theme

import androidx.compose.ui.graphics.Color

// --- Premium Brand Palette ---
val AzureBlue = Color(0xFF007AFF)
val ElectricBlue = Color(0xFF3B82F6)
val SoftAzure = Color(0xFFE3F2FD)

// --- Emotion-Aware Premium Colors ---
val MoodHappy = Color(0xFFFFD54F)    // Warm Gold
val MoodCalm = Color(0xFF64B5F6)     // Serene Blue
val MoodSad = Color(0xFF9575CD)      // Muted Lavender
val MoodAngry = Color(0xFFE57373)    // Soft Coral
val MoodFear = Color(0xFF81C784)     // Sage Green
val MoodSurprise = Color(0xFFBA68C8) // Soft Amethyst
val MoodDisgust = Color(0xFFAED581)  // Light Olive
val MoodContempt = Color(0xFFF06292) // Dusty Rose
val MoodNeutral = Color(0xFF90A4AE)  // Blue Grey

// --- Surfaces & Backgrounds ---
val BgLight = Color(0xFFF9FBFF)
val SurfaceLight = Color(0xFFFFFFFF)
val OnSurfaceLight = Color(0xFF1A1C1E)

val BgDark = Color(0xFF0B1117)
val SurfaceDark = Color(0xFF161C24)
val OnSurfaceDark = Color(0xFFF0F2F5)

// --- Material 3 Schemes ---
val LightPrimary = AzureBlue
val LightOnPrimary = Color.White
val LightBackground = BgLight
val LightSurface = SurfaceLight
val LightOutline = Color(0xFFD1D9E0)

val DarkPrimary = ElectricBlue
val DarkOnPrimary = Color.White
val DarkBackground = BgDark
val DarkSurface = SurfaceDark
val DarkOutline = Color(0xFF30363D)

// --- Gradients ---
val PremiumGradient = listOf(AzureBlue, ElectricBlue)
val HappyGradient = listOf(MoodHappy, Color(0xFFFFB300))
val CalmGradient = listOf(MoodCalm, Color(0xFF42A5F5))
val SadGradient = listOf(MoodSad, Color(0xFF7E57C2))
val AngryGradient = listOf(MoodAngry, Color(0xFFEF5350))
