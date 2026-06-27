package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.internal.resolveSeriesColor
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.BarGrouping
import io.github.komposeCharts.style.BarOrientation
import io.github.komposeCharts.style.ValueLabelPosition
import io.github.komposeCharts.theme.ChartTheme
import kotlin.math.abs
import kotlin.math.max

/**
 * Renders bars onto the canvas and returns hit-test rectangles.
 *
 * @return List of (seriesIndex, dataPoint, boundingRect) for hit testing.
 */
internal fun DrawScope.drawBars(
    data: ChartData,
    mapper: ChartCoordinateMapper,
    style: BarChartStyle,
    fractions: List<Float>,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    selectedBar: Pair<Int, Int>? = null,
): List<Triple<Int, DataPoint, Rect>> {
    return when (style.grouping) {
        BarGrouping.GROUPED -> drawGroupedBars(data, mapper, style, fractions, theme, textMeasurer, selectedBar)
        BarGrouping.STACKED -> drawStackedBars(data, mapper, style, fractions, theme, textMeasurer, selectedBar, percentMode = false)
        BarGrouping.PERCENT_STACKED -> drawStackedBars(data, mapper, style, fractions, theme, textMeasurer, selectedBar, percentMode = true)
    }
}

// ── Grouped Bars ──────────────────────────────────────────────────────

private fun DrawScope.drawGroupedBars(
    data: ChartData,
    mapper: ChartCoordinateMapper,
    style: BarChartStyle,
    fractions: List<Float>,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    selectedBar: Pair<Int, Int>?,
): List<Triple<Int, DataPoint, Rect>> {
    val rects = mutableListOf<Triple<Int, DataPoint, Rect>>()
    val visibleSeries = data.series
    val seriesCount = visibleSeries.size
    if (seriesCount == 0) return rects

    val categoryCount = visibleSeries.maxOf { it.points.size }
    if (categoryCount == 0) return rects

    val isVertical = style.orientation == BarOrientation.VERTICAL

    // Category and bar width calculations
    val totalPlotSize = if (isVertical) mapper.plotWidth else mapper.plotHeight
    val categoryWidth = totalPlotSize / categoryCount
    val groupWidth = categoryWidth * (1f - style.groupSpacing)
    val barWidth = (groupWidth / seriesCount) * (1f - style.barSpacing)
    val barGap = (groupWidth / seriesCount) * style.barSpacing

    val cornerRadius = CornerRadius(style.barCornerRadius.toPx())
    val minBarPx = style.minBarHeight.toPx()

    for (sIdx in visibleSeries.indices) {
        val series = visibleSeries[sIdx]
        val color = resolveSeriesColor(sIdx, series, theme, style.barColors)
        val gradient = style.barGradients?.getOrNull(sIdx)
        val fraction = fractions.getOrElse(sIdx) { 1f }

        for (catIdx in series.points.indices) {
            val point = series.points[catIdx]
            if (point.isMissing) continue

            val isDimmed = selectedBar != null && (selectedBar.first != sIdx || selectedBar.second != catIdx)
            val alpha = if (isDimmed) (style.selectionStyle?.dimAlpha ?: 1f) else 1f

            val rect = if (isVertical) {
                computeVerticalGroupedBarRect(
                    catIdx, sIdx, seriesCount, categoryWidth, groupWidth, barWidth, barGap,
                    mapper, point, fraction, minBarPx,
                )
            } else {
                computeHorizontalGroupedBarRect(
                    catIdx, sIdx, seriesCount, categoryWidth, groupWidth, barWidth, barGap,
                    mapper, point, fraction, minBarPx,
                )
            }

            drawSingleBar(rect, color, alpha, gradient, style, cornerRadius, isVertical)
            rects.add(Triple(sIdx, point, rect))

            // Value label
            if (style.valueLabelPosition != ValueLabelPosition.NONE) {
                drawValueLabel(point.y, rect, style, theme, textMeasurer, isVertical)
            }
        }
    }

    // Selection highlight stroke
    if (selectedBar != null) {
        drawSelectionStroke(selectedBar, rects, style, theme, cornerRadius)
    }

    return rects
}

private fun computeVerticalGroupedBarRect(
    catIdx: Int, sIdx: Int, seriesCount: Int,
    categoryWidth: Float, groupWidth: Float, barWidth: Float, barGap: Float,
    mapper: ChartCoordinateMapper, point: DataPoint, fraction: Float, minBarPx: Float,
): Rect {
    val categoryLeft = mapper.plotLeft + catIdx * categoryWidth + (categoryWidth - groupWidth) / 2f
    val barLeft = categoryLeft + sIdx * (barWidth + barGap)
    val baseY = mapper.baselineY
    val targetY = mapper.yToPixel(point.y)
    val animatedY = baseY + (targetY - baseY) * fraction

    val top = minOf(animatedY, baseY)
    val bottom = maxOf(animatedY, baseY)
    val height = max(bottom - top, minBarPx)

    return if (point.y >= 0f) {
        Rect(barLeft, bottom - height, barLeft + barWidth, bottom)
    } else {
        Rect(barLeft, top, barLeft + barWidth, top + height)
    }
}

private fun computeHorizontalGroupedBarRect(
    catIdx: Int, sIdx: Int, seriesCount: Int,
    categoryWidth: Float, groupWidth: Float, barWidth: Float, barGap: Float,
    mapper: ChartCoordinateMapper, point: DataPoint, fraction: Float, minBarPx: Float,
): Rect {
    val categoryTop = mapper.plotTop + catIdx * categoryWidth + (categoryWidth - groupWidth) / 2f
    val barTop = categoryTop + sIdx * (barWidth + barGap)
    val baseX = mapper.xToPixel(mapper.xRange.clamp(0f))
    val targetX = mapper.xToPixel(point.y) // for horizontal bars, y-value maps to x-pixel
    val animatedX = baseX + (targetX - baseX) * fraction

    val left = minOf(animatedX, baseX)
    val right = maxOf(animatedX, baseX)
    val width = max(right - left, minBarPx)

    return if (point.y >= 0f) {
        Rect(right - width, barTop, right, barTop + barWidth)
    } else {
        Rect(left, barTop, left + width, barTop + barWidth)
    }
}

// ── Stacked Bars ──────────────────────────────────────────────────────

private fun DrawScope.drawStackedBars(
    data: ChartData,
    mapper: ChartCoordinateMapper,
    style: BarChartStyle,
    fractions: List<Float>,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    selectedBar: Pair<Int, Int>?,
    percentMode: Boolean,
): List<Triple<Int, DataPoint, Rect>> {
    val rects = mutableListOf<Triple<Int, DataPoint, Rect>>()
    val visibleSeries = data.series
    val seriesCount = visibleSeries.size
    if (seriesCount == 0) return rects

    val categoryCount = visibleSeries.maxOf { it.points.size }
    if (categoryCount == 0) return rects

    val isVertical = style.orientation == BarOrientation.VERTICAL
    val totalPlotSize = if (isVertical) mapper.plotWidth else mapper.plotHeight
    val categoryWidth = totalPlotSize / categoryCount
    val barWidth = categoryWidth * (1f - style.groupSpacing)
    val cornerRadius = CornerRadius(style.barCornerRadius.toPx())
    val minBarPx = style.minBarHeight.toPx()

    // Pre-compute category totals for percent mode
    val categoryTotals = if (percentMode) {
        FloatArray(categoryCount) { catIdx ->
            visibleSeries.sumOf { series ->
                val y = series.points.getOrNull(catIdx)?.y ?: 0f
                if (y.isNaN()) 0.0 else abs(y.toDouble())
            }.toFloat()
        }
    } else null

    for (catIdx in 0 until categoryCount) {
        var positiveStackPx = 0f // accumulated pixel height going up (or right)
        var negativeStackPx = 0f // accumulated pixel height going down (or left)

        for (sIdx in visibleSeries.indices) {
            val series = visibleSeries[sIdx]
            val point = series.points.getOrNull(catIdx) ?: continue
            if (point.isMissing) continue

            val color = resolveSeriesColor(sIdx, series, theme, style.barColors)
            val gradient = style.barGradients?.getOrNull(sIdx)
            val fraction = fractions.getOrElse(sIdx) { 1f }
            val isDimmed = selectedBar != null && (selectedBar.first != sIdx || selectedBar.second != catIdx)
            val alpha = if (isDimmed) (style.selectionStyle?.dimAlpha ?: 1f) else 1f

            val value = if (percentMode && categoryTotals != null) {
                val total = categoryTotals[catIdx]
                if (total == 0f) 0f else (point.y / total) * 100f
            } else {
                point.y
            }

            val rect = if (isVertical) {
                computeVerticalStackedBarRect(
                    catIdx, categoryWidth, barWidth, mapper, value, fraction, minBarPx,
                    value >= 0f, positiveStackPx, negativeStackPx,
                ).also { rect ->
                    val segmentHeight = rect.height
                    if (value >= 0f) positiveStackPx += segmentHeight else negativeStackPx += segmentHeight
                }
            } else {
                computeHorizontalStackedBarRect(
                    catIdx, categoryWidth, barWidth, mapper, value, fraction, minBarPx,
                    value >= 0f, positiveStackPx, negativeStackPx,
                ).also { rect ->
                    val segmentWidth = rect.width
                    if (value >= 0f) positiveStackPx += segmentWidth else negativeStackPx += segmentWidth
                }
            }

            drawSingleBar(rect, color, alpha, gradient, style, cornerRadius, isVertical)
            rects.add(Triple(sIdx, point, rect))

            if (style.valueLabelPosition != ValueLabelPosition.NONE) {
                drawValueLabel(
                    if (percentMode) value else point.y,
                    rect, style, theme, textMeasurer, isVertical,
                )
            }
        }
    }

    if (selectedBar != null) {
        drawSelectionStroke(selectedBar, rects, style, theme, cornerRadius)
    }

    return rects
}

private fun computeVerticalStackedBarRect(
    catIdx: Int, categoryWidth: Float, barWidth: Float,
    mapper: ChartCoordinateMapper, value: Float, fraction: Float, minBarPx: Float,
    isPositive: Boolean, positiveStack: Float, negativeStack: Float,
): Rect {
    val categoryLeft = mapper.plotLeft + catIdx * categoryWidth + (categoryWidth - barWidth) / 2f
    val baseY = mapper.baselineY

    // Full segment height in pixels
    val fullHeightPx = abs(mapper.baselineY - mapper.yToPixel(abs(value)))
    val segmentHeight = max(fullHeightPx * fraction, if (fullHeightPx > 0f) minBarPx else 0f)

    return if (isPositive) {
        val bottom = baseY - positiveStack
        val top = bottom - segmentHeight
        Rect(categoryLeft, top, categoryLeft + barWidth, bottom)
    } else {
        val top = baseY + negativeStack
        Rect(categoryLeft, top, categoryLeft + barWidth, top + segmentHeight)
    }
}

private fun computeHorizontalStackedBarRect(
    catIdx: Int, categoryWidth: Float, barWidth: Float,
    mapper: ChartCoordinateMapper, value: Float, fraction: Float, minBarPx: Float,
    isPositive: Boolean, positiveStack: Float, negativeStack: Float,
): Rect {
    val categoryTop = mapper.plotTop + catIdx * categoryWidth + (categoryWidth - barWidth) / 2f
    val baseX = mapper.xToPixel(mapper.xRange.clamp(0f))

    val fullWidthPx = abs(baseX - mapper.xToPixel(abs(value)))
    val segmentWidth = max(fullWidthPx * fraction, if (fullWidthPx > 0f) minBarPx else 0f)

    return if (isPositive) {
        val left = baseX + positiveStack
        Rect(left, categoryTop, left + segmentWidth, categoryTop + barWidth)
    } else {
        val right = baseX - negativeStack
        Rect(right - segmentWidth, categoryTop, right, categoryTop + barWidth)
    }
}

// ── Drawing Helpers ───────────────────────────────────────────────────

private fun DrawScope.drawSingleBar(
    rect: Rect,
    color: Color,
    alpha: Float,
    gradient: io.github.komposeCharts.style.BarGradient?,
    style: BarChartStyle,
    cornerRadius: CornerRadius,
    isVertical: Boolean,
) {
    if (rect.width <= 0f || rect.height <= 0f) return

    val path = Path().apply {
        addRoundRect(RoundRect(rect, cornerRadius))
    }

    // Fill
    if (gradient != null) {
        val brush = if (isVertical) {
            Brush.linearGradient(
                colors = listOf(gradient.topColor, gradient.bottomColor),
                start = Offset(rect.left, rect.top),
                end = Offset(rect.left, rect.bottom),
            )
        } else {
            Brush.linearGradient(
                colors = listOf(gradient.topColor, gradient.bottomColor),
                start = Offset(rect.left, rect.top),
                end = Offset(rect.right, rect.top),
            )
        }
        drawPath(path, brush, alpha = alpha)
    } else {
        drawPath(path, color, alpha = alpha)
    }

    // Stroke
    if (style.barStrokeWidth.toPx() > 0f) {
        val strokeColor = style.barStrokeColor ?: color.copy(alpha = 0.7f)
        drawPath(path, strokeColor, alpha = alpha, style = Stroke(width = style.barStrokeWidth.toPx()))
    }
}

private fun DrawScope.drawValueLabel(
    value: Float,
    barRect: Rect,
    style: BarChartStyle,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    isVertical: Boolean,
) {
    val position = style.valueLabelPosition
    if (position == ValueLabelPosition.NONE) return

    val formatter = style.valueLabelFormatter ?: { v ->
        if (v == v.toLong().toFloat()) v.toLong().toString() else "${(v * 10).toInt() / 10.0}"
    }
    val text = formatter(value)
    val result = textMeasurer.measure(text, style = theme.labelTextStyle)
    val textWidth = result.size.width.toFloat()
    val textHeight = result.size.height.toFloat()

    val topLeft = if (isVertical) {
        val x = barRect.center.x - textWidth / 2f
        val y = when (position) {
            ValueLabelPosition.TOP -> barRect.top - textHeight - 2f
            ValueLabelPosition.CENTER -> barRect.center.y - textHeight / 2f
            ValueLabelPosition.INSIDE_BOTTOM -> barRect.bottom - textHeight - 4f
            ValueLabelPosition.OUTSIDE -> barRect.top - textHeight - 8f
            ValueLabelPosition.NONE -> return
        }
        Offset(x, y)
    } else {
        val y = barRect.center.y - textHeight / 2f
        val x = when (position) {
            ValueLabelPosition.TOP -> barRect.right + 4f
            ValueLabelPosition.CENTER -> barRect.center.x - textWidth / 2f
            ValueLabelPosition.INSIDE_BOTTOM -> barRect.left + 4f
            ValueLabelPosition.OUTSIDE -> barRect.right + 8f
            ValueLabelPosition.NONE -> return
        }
        Offset(x, y)
    }

    drawText(textLayoutResult = result, topLeft = topLeft)
}

private fun DrawScope.drawSelectionStroke(
    selectedBar: Pair<Int, Int>,
    rects: List<Triple<Int, DataPoint, Rect>>,
    style: BarChartStyle,
    theme: ChartTheme,
    cornerRadius: CornerRadius,
) {
    val selStyle = style.selectionStyle ?: return
    val match = rects.firstOrNull { it.first == selectedBar.first && it.second.x.toInt() == selectedBar.second }
        ?: return
    val rect = match.third
    val color = selStyle.highlightStrokeColor
        ?: resolveSeriesColor(match.first, DataPoint(0f, 0f).let {
            io.github.komposeCharts.core.data.DataSeries("", emptyList())
        }, theme, style.barColors)

    val path = Path().apply {
        addRoundRect(RoundRect(rect, cornerRadius))
    }
    drawPath(path, color, style = Stroke(width = selStyle.highlightStrokeWidth.toPx()))
}
