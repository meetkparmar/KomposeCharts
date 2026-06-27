package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.animation.ChartAnimation

/**
 * Complete visual and behavioral configuration for a line chart.
 *
 * All fields have sensible defaults — a plain `LineChartStyle()` produces
 * a working multi-series line chart with the theme palette, straight segments,
 * and a left-to-right reveal animation.
 */
data class LineChartStyle(
    // --- Colors & Line ---
    /** Per-series line colors. Null = use theme palette. */
    val lineColors: List<Color>? = null,
    val lineWidth: Dp = 2.dp,
    /** When true, segments are drawn as smooth cubic curves instead of straight lines. */
    val smooth: Boolean = false,

    // --- Markers ---
    val showMarkers: Boolean = false,
    /** Marker radius. */
    val markerSize: Dp = 5.dp,
    val markerShape: MarkerShape = MarkerShape.CIRCLE,

    // --- Area fill ---
    /** When true, the region between the line and the baseline is filled. */
    val areaFill: Boolean = false,
    /** Opacity of the area fill at the line; fades to transparent toward the baseline. */
    val areaAlpha: Float = 0.15f,

    // --- Interaction ---
    /** Selection highlight style. Null = no selection highlighting. */
    val selectionStyle: SelectionStyle? = null,
    /** Crosshair guidelines on pointer. Null = no crosshair. */
    val crosshairStyle: CrosshairStyle? = null,

    // --- Reference Lines ---
    val referenceLines: List<ReferenceLine> = emptyList(),

    // --- Sub-styles ---
    val axisStyle: AxisStyle = AxisStyle(),
    val legendStyle: LegendStyle = LegendStyle(),
    /** Tooltip on tap. Null = no tooltip. */
    val tooltipStyle: TooltipStyle? = null,
    val animation: ChartAnimation = ChartAnimation.Default,
)
