package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.animation.ChartAnimation

/** Interpolation style for line segments between data points. */
enum class CurveType {
    /** Straight line segments between points. */
    STRAIGHT,
    /** Cubic bezier curves (smooth, may overshoot). */
    BEZIER,
    /** Catmull-Rom splines (smooth, passes through all points). */
    CATMULL_ROM,
}

/**
 * Visual configuration for [LineChart].
 *
 * All nullable fields fall back to the ambient [ChartTheme] values when null.
 */
data class LineChartStyle(
    /** Per-series line colors. Null = use theme palette. */
    val lineColors: List<Color>? = null,
    val lineThickness: Dp = 2.dp,
    val curveType: CurveType = CurveType.CATMULL_ROM,
    val showArea: Boolean = false,
    /** Opacity of the area fill below the line (0f–1f). */
    val areaAlpha: Float = 0.15f,
    val showMarkers: Boolean = true,
    val markerRadius: Dp = 4.dp,
    val axisStyle: AxisStyle = AxisStyle(),
    val animation: ChartAnimation = ChartAnimation.Default,
)
