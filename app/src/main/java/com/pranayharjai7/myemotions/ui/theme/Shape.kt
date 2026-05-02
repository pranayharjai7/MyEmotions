package com.pranayharjai7.myemotions.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(24.dp), // ButtonShape radius
    large = RoundedCornerShape(28.dp),  // CardShape radius
    extraLarge = RoundedCornerShape(32.dp) // ContainerShape radius
)

val ButtonShape = RoundedCornerShape(24.dp)
val CardShape = RoundedCornerShape(28.dp)
val ContainerShape = RoundedCornerShape(32.dp)
