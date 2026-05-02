package com.pranayharjai7.myemotions.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Defines a visual theme profile for the app based on mood.
 */
data class MoodTheme(
    val name: String,
    val primaryColor: Color,
    val backgroundColor: Color = Bg_Dark,
    val accentColor: Color,
    val glassTintOpacity: Float = 0.18f,
    val description: String
)

// --- EMOTION THEME PROFILES ---

val HappinessTheme = MoodTheme(
    name = "Happiness",
    primaryColor = ThemeHappiness,
    accentColor = Color(0xFFFFC300),
    description = "Uplifting, energetic, optimistic"
)

val SadnessTheme = MoodTheme(
    name = "Sadness",
    primaryColor = ThemeSadness,
    accentColor = Color(0xFF818CF8),
    description = "Calm, reflective, deep"
)

val AngerTheme = MoodTheme(
    name = "Anger",
    primaryColor = ThemeAnger,
    accentColor = Color(0xFFDC2626),
    description = "Intense, powerful, focused"
)

val FearTheme = MoodTheme(
    name = "Fear",
    primaryColor = ThemeFear,
    accentColor = Color(0xFF059669),
    description = "Primal, alert, deep emerald"
)

val SurpriseTheme = MoodTheme(
    name = "Surprise",
    primaryColor = ThemeSurprise,
    accentColor = Color(0xFF7C3AED),
    description = "Vivid, sudden, high energy"
)

val DisgustTheme = MoodTheme(
    name = "Disgust",
    primaryColor = ThemeDisgust,
    accentColor = Color(0xFF65A30D),
    description = "Organic, earthy, rooted"
)

val ContemptTheme = MoodTheme(
    name = "Contempt",
    primaryColor = ThemeContempt,
    accentColor = Color(0xFFDB2777),
    description = "Superior, distant, muted rose"
)

val NeutralTheme = MoodTheme(
    name = "Neutral",
    primaryColor = ThemeNeutral,
    accentColor = AzureBlue,
    description = "Calm, baseline, balanced"
)

/**
 * The default Azure Glass theme profile.
 */
val DefaultAzureTheme = NeutralTheme

/**
 * Maps a detected emotion string to its corresponding MoodTheme profile.
 */
fun getThemeForEmotion(emotion: String?): MoodTheme {
    return when (emotion) {
        "Happiness" -> HappinessTheme
        "Sadness" -> SadnessTheme
        "Anger" -> AngerTheme
        "Fear" -> FearTheme
        "Surprise" -> SurpriseTheme
        "Disgust" -> DisgustTheme
        "Contempt" -> ContemptTheme
        "Neutral" -> NeutralTheme
        else -> DefaultAzureTheme
    }
}
