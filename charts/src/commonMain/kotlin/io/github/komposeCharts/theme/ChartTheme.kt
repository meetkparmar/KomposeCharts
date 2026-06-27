package io.github.komposeCharts.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visual theme for all chart composables.
 *
 * Provided down the composition tree via [LocalChartTheme].
 */
data class ChartTheme(
    /** Palette of colors cycled across series. */
    val colors: List<Color>,
    val backgroundColor: Color = Color.Transparent,
    val gridLineColor: Color = Color(0x1A000000),
    val axisLineColor: Color = Color(0x33000000),
    val labelTextStyle: TextStyle = TextStyle(fontSize = 11.sp),
    val titleTextStyle: TextStyle = TextStyle(fontSize = 14.sp),
    val legendTextStyle: TextStyle = TextStyle(fontSize = 12.sp),
    val contentPadding: Dp = 8.dp,
)
