package io.github.komposeCharts.style

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Configuration for chart axis rendering.
 */
data class AxisStyle(
    val showXAxis: Boolean = true,
    val showYAxis: Boolean = true,
    val showXLabels: Boolean = true,
    val showYLabels: Boolean = true,
    val showGrid: Boolean = true,
    val yTickCount: Int = 5,
    val yLabelFormatter: ((Float) -> String)? = null,
    val xLabelFormatter: ((Float) -> String)? = null,
    val axisLineThicknessDp: Dp = 1.dp,
    val gridLineThicknessDp: Dp = 0.5.dp,
)
