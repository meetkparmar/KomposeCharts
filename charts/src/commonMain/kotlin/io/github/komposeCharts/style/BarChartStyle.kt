package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.animation.ChartAnimation

/**
 * Complete visual and behavioral configuration for a bar chart.
 *
 * All fields have sensible defaults — a plain `BarChartStyle()` produces
 * a working chart with the theme palette, grouped vertical bars, and smooth animation.
 */
data class BarChartStyle(
    // --- Colors & Fill ---
    /** Per-series solid colors. Null = use theme palette. */
    val barColors: List<Color>? = null,
    /** Per-series gradient fills. Overrides [barColors] for matching indices. */
    val barGradients: List<BarGradient>? = null,
    /** Stroke width around bars. 0 = no stroke. */
    val barStrokeWidth: Dp = 0.dp,
    /** Stroke color. Null = derived from bar color at reduced alpha. */
    val barStrokeColor: Color? = null,

    // --- Shape ---
    val barCornerRadius: Dp = 4.dp,
    /** Minimum rendered height for very small values. */
    val minBarHeight: Dp = 0.dp,

    // --- Layout ---
    /** Fraction of category width used as gap between groups (0..1). */
    val groupSpacing: Float = 0.3f,
    /** Fraction of group width used as gap between bars within a group (0..1). */
    val barSpacing: Float = 0.05f,
    val orientation: BarOrientation = BarOrientation.VERTICAL,
    /**
     * When true and [orientation] is [BarOrientation.HORIZONTAL], swaps the axes so the
     * value axis runs horizontally (along the bottom) and the category axis runs vertically
     * (along the left). This is the correct layout for horizontal bar charts; enable it when
     * using [BarOrientation.HORIZONTAL]. Has no effect for vertical charts.
     */
    val swapAxes: Boolean = false,
    val grouping: BarGrouping = BarGrouping.GROUPED,

    // --- Labels ---
    val valueLabelPosition: ValueLabelPosition = ValueLabelPosition.NONE,
    /** Custom formatter for value labels. Null = default decimal formatting. */
    val valueLabelFormatter: ((Float) -> String)? = null,

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
    /** Tooltip on tap/hover. Null = no tooltip. */
    val tooltipStyle: TooltipStyle? = null,
    val animation: ChartAnimation = ChartAnimation.Default,
)
