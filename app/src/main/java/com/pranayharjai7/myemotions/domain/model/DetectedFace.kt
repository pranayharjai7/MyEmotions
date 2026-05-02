package com.pranayharjai7.myemotions.domain.model

import android.graphics.Rect

data class DetectedFace(
    val bounds: Rect,
    val landmarks: FloatArray? = null
)
