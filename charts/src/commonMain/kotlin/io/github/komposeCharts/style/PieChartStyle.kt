package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.animation.ChartAnimation

/**
 * Label content rendered on each pie slice.
 */
enum class PieLabelType {
    PERCENT,
    VALUE,
    NAME,
    NAME_PERCENT,
}

/**
 * Complete visual and behavioral configuration for a pie / donut chart.
 *
 * A pie chart renders the first series of [io.github.komposeCharts.core.data.ChartData];
 * each [io.github.komposeCharts.core.data.DataPoint] becomes one slice (its `y` is the
 * slice size and its `label` the category name).
 */
data class PieChartStyle(
    // --- Colors ---
    /** Per-slice colors. Null = use theme palette. */
    val sliceColors: List<Color>? = null,
    /** Separator stroke between slices. 0 = none. */
    val sliceStrokeWidth: Dp = 1.dp,
    /** Separator stroke color. Null = no separator. */
    val sliceStrokeColor: Color? = null,

    // --- Geometry ---
    /** Angle of the first slice's start edge, in degrees (-90 = top). */
    val startAngle: Float = -90f,
    /** When true, renders a donut (hole) instead of a full pie. */
    val donut: Boolean = false,
    /** Hole radius as a fraction of the outer radius (used when [donut]). */
    val innerRadiusFraction: Float = 0.55f,

    // --- Labels ---
    val showLabels: Boolean = true,
    val labelType: PieLabelType = PieLabelType.PERCENT,

    // --- Interaction ---
    /** Selection highlight (explodes the selected slice). Null = no selection. */
    val selectionStyle: SelectionStyle? = null,

    // --- Sub-styles ---
    val legendStyle: LegendStyle = LegendStyle(),
    /** Tooltip on tap. Null = no tooltip. */
    val tooltipStyle: TooltipStyle? = null,
    val animation: ChartAnimation = ChartAnimation.Default,
)
