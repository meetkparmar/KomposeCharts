package io.github.komposeCharts.renderer

import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.style.AxisStyle
import io.github.komposeCharts.theme.ChartTheme

/**
 * Draws horizontal grid lines at the given y-tick positions.
 */
internal fun DrawScope.drawGrid(
    yTicks: List<Float>,
    mapper: ChartCoordinateMapper,
    style: AxisStyle,
    theme: ChartTheme,
) {
    if (!style.showGrid) return

    val strokeWidth = style.gridLineThicknessDp.toPx()
    val color = theme.gridLineColor

    for (tick in yTicks) {
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
