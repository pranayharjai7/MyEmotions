package com.pranayharjai7.myemotions.ui.utils

fun getEmojiForEmotion(emotion: String): String {
    return when (emotion.lowercase()) {
        "happiness" -> "😊"
        "calm" -> "😌"
        "sadness" -> "😔"
        "anger" -> "😡"
        "fear" -> "😨"
        "surprise" -> "😲"
        "disgust" -> "🤢"
        "contempt" -> "😒"
        "neutral" -> "😐"
        else -> "❓"
    }
}
