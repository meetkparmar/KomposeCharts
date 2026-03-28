package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Controls how axes and grid lines are drawn for Cartesian charts (Line, Bar).
 */
data class AxisStyle(
    val showXAxis: Boolean = true,
    val showYAxis: Boolean = true,
    val showXLabels: Boolean = true,
    val showYLabels: Boolean = true,
    val showGrid: Boolean = true,
    /** Desired number of y-axis ticks (actual count may vary due to nice rounding). */
    val yTickCount: Int = 5,
    /** Custom label formatter for y-axis ticks. Null = default numeric formatting. */
    val yLabelFormatter: ((Float) -> String)? = null,
    /** Custom label formatter for x-axis ticks. Null = uses DataPoint.label or x value. */
    val xLabelFormatter: ((Float) -> String)? = null,
    val axisLineThicknessDp: Dp = 1.dp,
    val gridLineThicknessDp: Dp = 0.5.dp,
)
