package io.github.komposeCharts.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Defines the visual appearance for all charts within a scope.
 *
 * Apply globally via [ChartTheme] composable or per-chart via style objects.
 */
data class ChartTheme(
    /** Ordered color palette cycled across data series. */
    val colors: List<Color>,
    /** Chart canvas background (transparent by default — inherits surface). */
    val backgroundColor: Color = Color.Transparent,
    /** Color for grid lines. */
    val gridLineColor: Color = Color(0x1A000000),
    /** Color for axis lines. */
    val axisLineColor: Color = Color(0x33000000),
    /** Text style for axis tick labels. */
    val labelTextStyle: TextStyle = TextStyle(fontSize = 11.sp),
    /** Text style for chart titles. */
    val titleTextStyle: TextStyle = TextStyle(fontSize = 14.sp),
    /** Text style for legend labels. */
    val legendTextStyle: TextStyle = TextStyle(fontSize = 12.sp),
    /** Inner padding around the chart drawing area. */
    val contentPadding: Dp = 8.dp,
)
