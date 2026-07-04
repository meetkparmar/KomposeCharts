package io.github.komposeCharts.renderer

import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.style.AxisStyle
import io.github.komposeCharts.theme.ChartTheme

/**
 * Draws grid lines at the given value-tick positions.
 *
 * By default the lines are horizontal (one per y-tick). When [swapAxes] is true the value axis
 * runs horizontally, so the lines are drawn vertically (one per tick, positioned via the mapper's
 * x-axis) — the correct layout for horizontal bar charts.
 */
internal fun DrawScope.drawGrid(
    valueTicks: List<Float>,
    mapper: ChartCoordinateMapper,
    style: AxisStyle,
    theme: ChartTheme,
    swapAxes: Boolean = false,
) {
    if (!style.showGrid) return

    val strokeWidth = style.gridLineThicknessDp.toPx()
    val color = theme.gridLineColor

    for (tick in valueTicks) {
        if (swapAxes) {
            val x = mapper.xToPixel(tick)
            if (x in mapper.plotLeft..mapper.plotRight) {
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(x, mapper.plotTop),
                    end = androidx.compose.ui.geometry.Offset(x, mapper.plotBottom),
                    strokeWidth = strokeWidth,
                )
            }
        } else {
            val y = mapper.yToPixel(tick)
            if (y in mapper.plotTop..mapper.plotBottom) {
                drawLine(
                    color = color,
                    start = androidx.compose.ui.geometry.Offset(mapper.plotLeft, y),
                    end = androidx.compose.ui.geometry.Offset(mapper.plotRight, y),
                    strokeWidth = strokeWidth,
                )
            }
        }
    }
}
