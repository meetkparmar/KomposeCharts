package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color

/**
 * Gradient fill for a bar, applied as a linear gradient from top to bottom
 * (or left to right for horizontal bars).
 */
data class BarGradient(
    val topColor: Color,
    val bottomColor: Color,
)
