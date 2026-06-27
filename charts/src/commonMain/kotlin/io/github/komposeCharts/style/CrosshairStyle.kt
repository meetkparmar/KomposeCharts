package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Style for crosshair guidelines shown on pointer interaction.
 */
data class CrosshairStyle(
    val showVertical: Boolean = true,
    val showHorizontal: Boolean = true,
    val color: Color = Color.Gray.copy(alpha = 0.5f),
    val strokeWidth: Dp = 0.5.dp,
    /** Dash pattern in pixels (e.g. [4f, 4f]). Null = solid line. */
    val dashPattern: List<Float>? = listOf(4f, 4f),
)
