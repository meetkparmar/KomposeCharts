package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.style.AxisStyle
import io.github.komposeCharts.theme.ChartTheme

/**
 * Draws horizontal grid lines at the provided y-tick positions.
 */
internal fun DrawScope.drawGrid(
    mapper: ChartCoordinateMapper,
    yTicks: List<Float>,
    style: AxisStyle,
    theme: ChartTheme,
) {
    if (!style.showGrid) return

    val strokePx = style.gridLineThicknessDp.toPx()

    for (tick in yTicks) {
        val y = mapper.yToPixel(tick)
        drawLine(
            color = theme.gridLineColor,
            start = Offset(mapper.plotLeft, y),
            end = Offset(mapper.plotRight, y),
            strokeWidth = strokePx,
        )
    }
}
