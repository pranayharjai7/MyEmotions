package com.pranayharjai7.myemotions.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Defines a visual theme profile for the app based on mood.
 */
data class MoodTheme(
    val name: String,
    val primaryColor: Color,
    val backgroundColor: Color,
    val accentColor: Color,
    val gradient: List<Color>,
    val description: String
)

// --- EMOTION THEME PROFILES ---

val HappinessTheme = MoodTheme(
    name = "Happiness",
    primaryColor = MoodHappy,
    backgroundColor = Color(0xFFFFFDE7),
    accentColor = Color(0xFFFFB300),
    gradient = HappyGradient,
    description = "Uplifting, energetic, optimistic"
)

val CalmTheme = MoodTheme(
    name = "Calm",
    primaryColor = MoodCalm,
    backgroundColor = Color(0xFFE3F2FD),
    accentColor = Color(0xFF42A5F5),
    gradient = CalmGradient,
    description = "Peaceful, serene, balanced"
)

val SadnessTheme = MoodTheme(
    name = "Sadness",
    primaryColor = MoodSad,
    backgroundColor = Color(0xFFEDE7F6),
    accentColor = Color(0xFF7E57C2),
    gradient = SadGradient,
    description = "Reflective, quiet, deep"
)

val AngerTheme = MoodTheme(
    name = "Anger",
    primaryColor = MoodAngry,
    backgroundColor = Color(0xFFFFEBEE),
    accentColor = Color(0xFFEF5350),
    gradient = AngryGradient,
    description = "Intense, powerful, focused"
)

val FearTheme = MoodTheme(
    name = "Fear",
    primaryColor = MoodFear,
    backgroundColor = Color(0xFFE8F5E9),
    accentColor = Color(0xFF66BB6A),
    gradient = listOf(MoodFear, Color(0xFF4CAF50)),
    description = "Alert, cautious, aware"
)

val SurpriseTheme = MoodTheme(
    name = "Surprise",
    primaryColor = MoodSurprise,
    backgroundColor = Color(0xFFF3E5F5),
    accentColor = Color(0xFFAB47BC),
    gradient = listOf(MoodSurprise, Color(0xFF9C27B0)),
    description = "Sudden, vivid, energetic"
)

val DisgustTheme = MoodTheme(
    name = "Disgust",
    primaryColor = MoodDisgust,
    backgroundColor = Color(0xFFF1F8E9),
    accentColor = Color(0xFF8BC34A),
    gradient = listOf(MoodDisgust, Color(0xFF7CB342)),
    description = "Rooted, organic, earthy"
)

val ContemptTheme = MoodTheme(
    name = "Contempt",
    primaryColor = MoodContempt,
    backgroundColor = Color(0xFFFCE4EC),
    accentColor = Color(0xFFF06292),
    gradient = listOf(MoodContempt, Color(0xFFE91E63)),
    description = "Distant, analytical, superior"
)

val NeutralTheme = MoodTheme(
    name = "Neutral",
    primaryColor = AzureBlue,
    backgroundColor = BgLight,
    accentColor = ElectricBlue,
    gradient = PremiumGradient,
    description = "Balanced, calm, steady"
)

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
