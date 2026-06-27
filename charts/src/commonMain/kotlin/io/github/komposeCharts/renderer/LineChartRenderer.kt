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
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.internal.resolveSeriesColor
import io.github.komposeCharts.style.LineChartStyle
import io.github.komposeCharts.style.MarkerShape
import io.github.komposeCharts.theme.ChartTheme

/**
 * Renders line series (with optional markers and area fill) onto the canvas
 * and returns hit-test rectangles for each visible data point.
 *
 * Entry animation is a left-to-right reveal driven by per-series [fractions].
 *
 * @return List of (seriesIndex, dataPoint, boundingRect) for hit testing.
 */
internal fun DrawScope.drawLines(
    data: ChartData,
    mapper: ChartCoordinateMapper,
    style: LineChartStyle,
    fractions: List<Float>,
    theme: ChartTheme,
    selectedPoint: Pair<Int, Int>? = null,
): List<Triple<Int, DataPoint, Rect>> {
    val rects = mutableListOf<Triple<Int, DataPoint, Rect>>()
    val lineWidthPx = style.lineWidth.toPx()
    val markerRadiusPx = style.markerSize.toPx()
    val hitRadiusPx = maxOf(markerRadiusPx, 12.dp.toPx())

    data.series.forEachIndexed { sIdx, series ->
        val color = resolveSeriesColor(sIdx, series, theme, style.lineColors)
        val fraction = fractions.getOrElse(sIdx) { 1f }
        val seriesDimmed = selectedPoint != null &&
            style.selectionStyle != null &&
            selectedPoint.first != sIdx
        val seriesAlpha = if (seriesDimmed) style.selectionStyle!!.dimAlpha else 1f

        // Group contiguous non-missing points into runs (gaps break the line).
        val runs = mutableListOf<List<DataPoint>>()
        var current = mutableListOf<DataPoint>()
        for (p in series.points) {
            if (p.isMissing) {
                if (current.isNotEmpty()) { runs.add(current); current = mutableListOf() }
            } else {
                current.add(p)
            }
        }
        if (current.isNotEmpty()) runs.add(current)

        // Reveal clip: left-to-right.
        val revealRight = mapper.plotLeft + mapper.plotWidth * fraction
        clipRect(right = revealRight.coerceAtLeast(mapper.plotLeft)) {
            for (run in runs) {
                val offsets = run.map { mapper.toOffset(it) }
                if (offsets.isEmpty()) continue

                val linePath = buildLinePath(offsets, style.smooth)

                // Area fill below the line.
                if (style.areaFill && offsets.size >= 1) {
                    val areaPath = Path().apply {
                        addPath(linePath)
                        lineTo(offsets.last().x, mapper.baselineY)
                        lineTo(offsets.first().x, mapper.baselineY)
                        close()
                    }
                    val brush = Brush.verticalGradient(
                        colors = listOf(color.copy(alpha = style.areaAlpha), Color.Transparent),
                        startY = mapper.plotTop,
                        endY = mapper.baselineY,
                    )
                    drawPath(areaPath, brush, alpha = seriesAlpha)
                }

                // Line stroke.
                if (offsets.size >= 2) {
                    drawPath(
                        linePath,
                        color,
                        alpha = seriesAlpha,
                        style = Stroke(width = lineWidthPx),
                    )
                }

                // Markers.
                if (style.showMarkers) {
                    for (offset in offsets) {
                        drawMarker(offset, markerRadiusPx, style.markerShape, color, seriesAlpha)
                    }
                }
            }

            // Selected point highlight (drawn even when markers are off).
            if (selectedPoint != null && selectedPoint.first == sIdx && style.selectionStyle != null) {
                val sel = series.points.firstOrNull {
                    !it.isMissing && it.x.toInt() == selectedPoint.second
                }
                if (sel != null) {
                    val center = mapper.toOffset(sel)
                    val r = markerRadiusPx * 1.6f
                    drawCircle(color, radius = r, center = center)
                    val strokeColor = style.selectionStyle.highlightStrokeColor ?: Color.White
                    drawCircle(
                        strokeColor,
                        radius = r,
                        center = center,
                        style = Stroke(width = style.selectionStyle.highlightStrokeWidth.toPx()),
                    )
                }
            }
        }

        // Hit-test rects (full geometry, independent of reveal clip).
        for (p in series.points) {
            if (p.isMissing) continue
            val o = mapper.toOffset(p)
            rects.add(
                Triple(
                    sIdx,
                    p,
                    Rect(o.x - hitRadiusPx, o.y - hitRadiusPx, o.x + hitRadiusPx, o.y + hitRadiusPx),
                ),
            )
        }
    }

    return rects
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
