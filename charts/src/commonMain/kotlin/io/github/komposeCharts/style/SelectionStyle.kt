package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Visual style for bar selection/hover highlighting.
 *
 * When a bar is selected, it renders at [highlightAlpha] while all other bars
 * are dimmed to [dimAlpha]. An optional stroke is drawn around the selected bar.
 */
data class SelectionStyle(
    val highlightAlpha: Float = 1.0f,
    val dimAlpha: Float = 0.3f,
    val highlightStrokeWidth: Dp = 2.dp,
    /** Stroke color for the selected bar. Null = use the bar's own color. */
    val highlightStrokeColor: Color? = null,
)
