package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.clipRect
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.style.CurveType
import io.github.komposeCharts.style.LineChartStyle

/**
 * Renders a single data series as a line (and optionally an area fill) onto the DrawScope canvas.
 *
 * The animation technique is a clip-rect that expands from left to right:
 *   `clipRect(right = plotWidth * fraction)`
 * This works on all Skia targets without requiring PathMeasure.
 */
internal fun DrawScope.drawLineSeries(
    series: DataSeries,
    color: Color,
    mapper: ChartCoordinateMapper,
    style: LineChartStyle,
    fraction: Float,
) {
    if (series.points.size < 2) return

    val offsets = series.points.map { mapper.toOffset(it) }
    val path = buildLinePath(offsets, style.curveType)

    clipRect(
        left = mapper.plotLeft,
        top = mapper.plotTop,
        right = mapper.plotLeft + mapper.plotWidth * fraction,
        bottom = mapper.plotBottom,
    ) {
        // Area fill
        if (style.showArea) {
            val areaPath = Path().apply {
                addPath(path)
                lineTo(offsets.last().x, mapper.baselineY)
                lineTo(offsets.first().x, mapper.baselineY)
                close()
            }
            drawPath(areaPath, color = color.copy(alpha = style.areaAlpha))
        }

        // Line stroke
        drawPath(path, color = color, style = androidx.compose.ui.graphics.drawscope.Stroke(
            width = style.lineThickness.toPx()
        ))
    }

    // Markers (drawn outside clip so they're always fully visible at the animation edge)
    if (style.showMarkers) {
        val visibleCount = (offsets.size * fraction).toInt().coerceIn(0, offsets.size)
        for (i in 0 until visibleCount) {
            drawCircle(
                color = color,
                radius = style.markerRadius.toPx(),
                center = offsets[i],
            )
        }
    }
}

private fun buildLinePath(offsets: List<Offset>, curveType: CurveType): Path {
    val path = Path()
    if (offsets.isEmpty()) return path
    path.moveTo(offsets[0].x, offsets[0].y)

    when (curveType) {
        CurveType.STRAIGHT -> {
            for (i in 1 until offsets.size) {
                path.lineTo(offsets[i].x, offsets[i].y)
            }
        }
        CurveType.BEZIER -> {
            for (i in 1 until offsets.size) {
                val prev = offsets[i - 1]
                val curr = offsets[i]
                val cpX = (prev.x + curr.x) / 2f
                path.cubicTo(cpX, prev.y, cpX, curr.y, curr.x, curr.y)
            }
        }
        CurveType.CATMULL_ROM -> {
            // Catmull-Rom spline converted to cubic bezier control points
            for (i in 1 until offsets.size) {
                val p0 = offsets.getOrElse(i - 2) { offsets[i - 1] }
                val p1 = offsets[i - 1]
                val p2 = offsets[i]
                val p3 = offsets.getOrElse(i + 1) { offsets[i] }

                val cp1x = p1.x + (p2.x - p0.x) / 6f
                val cp1y = p1.y + (p2.y - p0.y) / 6f
                val cp2x = p2.x - (p3.x - p1.x) / 6f
                val cp2y = p2.y - (p3.y - p1.y) / 6f

                path.cubicTo(cp1x, cp1y, cp2x, cp2y, p2.x, p2.y)
            }
        }
    }
    return path
}
