package io.github.komposeCharts.style

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Visual configuration for the data callout tooltip shown on tap.
 *
 * Add this to a chart's style to opt in to tooltips:
 * ```
 * LineChart(style = LineChartStyle(tooltipStyle = TooltipStyle()))
 * ```
 *
 * @param backgroundAlpha Opacity of the tooltip background surface (0f–1f).
 * @param cornerRadius Corner rounding radius of the tooltip card.
 * @param padding Internal content padding inside the tooltip card.
 * @param textStyle Text style for the label and value lines.
 * @param dismissAfterMs Auto-dismiss delay in milliseconds. Use 0 to never auto-dismiss.
 */
data class TooltipStyle(
    val backgroundAlpha: Float = 0.9f,
    val cornerRadius: Dp = 8.dp,
    val padding: Dp = 8.dp,
    val textStyle: TextStyle = TextStyle(fontSize = 12.sp),
    val dismissAfterMs: Long = 2000L,
)
