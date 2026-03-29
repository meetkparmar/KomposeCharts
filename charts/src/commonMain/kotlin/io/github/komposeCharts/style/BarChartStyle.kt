package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.animation.ChartAnimation

/** Axis orientation for bar charts. */
enum class BarOrientation { VERTICAL, HORIZONTAL }

/** How multiple series are laid out within a category group. */
enum class BarGrouping { GROUPED, STACKED }

/**
 * Visual configuration for [BarChart].
 *
 * All nullable fields fall back to the ambient [ChartTheme] values when null.
 */
data class BarChartStyle(
    /** Per-series bar colors. Null = use theme palette. */
    val barColors: List<Color>? = null,
    val barCornerRadius: Dp = 4.dp,
    /** Fractional gap between category groups (0f = no gap, 1f = full bar width). */
    val groupSpacing: Float = 0.3f,
    /** Fractional gap between bars within a group. */
    val barSpacing: Float = 0.05f,
    val orientation: BarOrientation = BarOrientation.VERTICAL,
    val grouping: BarGrouping = BarGrouping.GROUPED,
    val showValueLabels: Boolean = false,
    val axisStyle: AxisStyle = AxisStyle(),
    val animation: ChartAnimation = ChartAnimation.Default,
    val legendStyle: LegendStyle = LegendStyle(),
    /** Set to a [TooltipStyle] instance to enable data callout tooltips on tap. */
    val tooltipStyle: TooltipStyle? = null,
)
