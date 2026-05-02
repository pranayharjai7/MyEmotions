package com.pranayharjai7.myemotions.domain.model

enum class EmotionType(val label: String) {
    HAPPY("Happiness"),
    SAD("Sadness"),
    ANGRY("Anger"),
    CONTEMPT("Contempt"),
    SURPRISED("Surprise"),
    NEUTRAL("Neutral"),
    FEAR("Fear"),
    DISGUST("Disgust"),
    UNKNOWN("Unknown");

    companion object {
        fun fromLabel(label: String): EmotionType {
            return entries.find { it.label.equals(label, ignoreCase = true) } ?: UNKNOWN
        }
    }
}
