package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.animation.ChartAnimation

/** What text is shown on each pie/donut slice. */
enum class SliceLabelType {
    NONE,
    /** Shows the percentage of the total. */
    PERCENT,
    /** Shows the raw y-value. */
    VALUE,
    /** Shows the DataPoint.label string. */
    LABEL,
}

/**
 * Visual configuration for [PieChart].
 *
 * Set [innerRadiusFraction] > 0 to render as a donut chart.
 * All nullable fields fall back to the ambient [ChartTheme] values when null.
 */
data class PieChartStyle(
    /** Per-slice colors (one per DataPoint in the first series). Null = use theme palette. */
    val sliceColors: List<Color>? = null,
    /** Gap in degrees between adjacent slices. */
    val sliceSpacingDeg: Float = 1f,
    /** 0f = full pie, 0.5f = donut with 50% hole, etc. */
    val innerRadiusFraction: Float = 0f,
    /** Angle (degrees) where the first slice starts. 0 = top, 90 = right. */
    val startAngleDeg: Float = -90f,
    val sliceLabelType: SliceLabelType = SliceLabelType.PERCENT,
    /** Text shown in the center hole (only visible when innerRadiusFraction > 0). */
    val centerLabel: String? = null,
    val animation: ChartAnimation = ChartAnimation.Default,
)
