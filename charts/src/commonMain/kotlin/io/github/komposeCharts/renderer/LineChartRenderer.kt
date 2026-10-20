package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.internal.ChartHit
import io.github.komposeCharts.internal.lineGeometry
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.internal.resolveSeriesColor
import io.github.komposeCharts.style.LineChartStyle
import io.github.komposeCharts.style.MarkerShape
import io.github.komposeCharts.theme.ChartTheme

internal fun DrawScope.drawLines(
    data: ChartData,
    mapper: ChartCoordinateMapper,
    style: LineChartStyle,
    fractions: List<Float>,
    theme: ChartTheme,
    selectedPoint: Pair<Int, Int>? = null,
): List<ChartHit> {
    data.series.forEachIndexed { index, series ->
        val color = resolveSeriesColor(index, series, theme, style.lineColors)
        val dimmed = selectedPoint != null && selectedPoint.first != index
        val alpha = if (dimmed) style.selectionStyle?.dimAlpha ?: 1f else style.selectionStyle?.highlightAlpha ?: 1f
        val right = mapper.plotLeft + mapper.plotWidth * fractions.getOrElse(index) { 1f }
        clipRect(right = right.coerceAtLeast(mapper.plotLeft)) {
            validRuns(series).forEach { run -> drawLineRun(run.map(mapper::toOffset), mapper, style, color, alpha) }
            if (selectedPoint?.first == index) {
                series.points.getOrNull(selectedPoint.second)?.takeUnless { it.isMissing }?.let {
                    drawSelectedPoint(mapper.toOffset(it), style, color)
                }
            }
        }
    }
    return lineGeometry(data, mapper, style, theme, maxOf(style.markerSize.toPx(), 12.dp.toPx()))
}

private fun validRuns(series: DataSeries): List<List<DataPoint>> {
    val runs = mutableListOf<List<DataPoint>>()
    var current = mutableListOf<DataPoint>()
    for (point in series.points) {
        if (point.isMissing) {
            if (current.isNotEmpty()) runs.add(current)
            current = mutableListOf()
        } else current.add(point)
    }
    if (current.isNotEmpty()) runs.add(current)
    return runs
}

private fun DrawScope.drawLineRun(
    offsets: List<Offset>,
    mapper: ChartCoordinateMapper,
    style: LineChartStyle,
    color: Color,
    alpha: Float,
) {
    val path = buildLinePath(offsets, style.smooth)
    if (style.areaFill) {
        val area = Path().apply {
            addPath(path)
            lineTo(offsets.last().x, mapper.baselineY)
            lineTo(offsets.first().x, mapper.baselineY)
            close()
        }
        val brush = Brush.verticalGradient(
            listOf(color.copy(alpha = style.areaAlpha), Color.Transparent),
            startY = mapper.plotTop, endY = maxOf(mapper.plotTop + 1f, mapper.baselineY),
        )
        drawPath(area, brush, alpha = alpha)
    }
    if (offsets.size >= 2) drawPath(path, color, alpha = alpha, style = Stroke(style.lineWidth.toPx()))
    if (style.showMarkers) offsets.forEach { drawMarker(it, style.markerSize.toPx(), style.markerShape, color, alpha) }
}

private fun DrawScope.drawSelectedPoint(center: Offset, style: LineChartStyle, color: Color) {
    val selection = style.selectionStyle ?: return
    val radius = style.markerSize.toPx() * 1.6f
    drawCircle(color, radius, center, alpha = selection.highlightAlpha)
    drawCircle(
        selection.highlightStrokeColor ?: Color.White, radius, center,
        style = Stroke(selection.highlightStrokeWidth.toPx()),
    )
}

/** Builds a polyline or smooth-curve path through [offsets]. */
private fun buildLinePath(offsets: List<Offset>, smooth: Boolean): Path {
    val path = Path()
    if (offsets.isEmpty()) return path
    path.moveTo(offsets[0].x, offsets[0].y)
    if (offsets.size == 1) return path

    if (!smooth) {
        for (i in 1 until offsets.size) {
            path.lineTo(offsets[i].x, offsets[i].y)
        }
    } else {
        // Catmull-Rom → cubic Bézier.
        for (i in 0 until offsets.size - 1) {
            val p0 = offsets[if (i == 0) 0 else i - 1]
            val p1 = offsets[i]
            val p2 = offsets[i + 1]
            val p3 = offsets[if (i + 2 < offsets.size) i + 2 else offsets.size - 1]

            val c1 = Offset(p1.x + (p2.x - p0.x) / 6f, p1.y + (p2.y - p0.y) / 6f)
            val c2 = Offset(p2.x - (p3.x - p1.x) / 6f, p2.y - (p3.y - p1.y) / 6f)
            path.cubicTo(c1.x, c1.y, c2.x, c2.y, p2.x, p2.y)
        }
    }
    return path
}

private fun DrawScope.drawMarker(
    center: Offset,
    radius: Float,
    shape: MarkerShape,
    color: Color,
    alpha: Float,
) {
    when (shape) {
        MarkerShape.CIRCLE -> drawCircle(color, radius = radius, center = center, alpha = alpha)
        MarkerShape.SQUARE -> drawRect(
            color,
            topLeft = Offset(center.x - radius, center.y - radius),
            size = Size(radius * 2f, radius * 2f),
            alpha = alpha,
        )
        MarkerShape.DIAMOND -> {
            val path = Path().apply {
                moveTo(center.x, center.y - radius)
                lineTo(center.x + radius, center.y)
                lineTo(center.x, center.y + radius)
                lineTo(center.x - radius, center.y)
                close()
            }
            drawPath(path, color, alpha = alpha)
        }
    }
}
