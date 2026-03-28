package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.BarGrouping
import io.github.komposeCharts.style.BarOrientation
import io.github.komposeCharts.theme.ChartTheme

/**
 * Renders all bar series from [data] onto the DrawScope canvas.
 *
 * Animation: bar height grows via `lerp(baseline, top, fraction)` — bars grow upward.
 *
 * @param fractions Per-series animation fraction list (one per series, or a single shared value).
 */
internal fun DrawScope.drawBars(
    data: ChartData,
    colors: List<Color>,
    mapper: ChartCoordinateMapper,
    style: BarChartStyle,
    fractions: List<Float>,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
) {
    val seriesCount = data.series.size
    val categoryCount = data.series.maxOf { it.points.size }
    if (categoryCount == 0) return

    val categoryWidthPx = mapper.plotWidth / categoryCount
    val groupGap = categoryWidthPx * style.groupSpacing
    val groupWidthPx = categoryWidthPx - groupGap
    val barGap = groupWidthPx * style.barSpacing
    val barWidthPx = if (seriesCount > 0) {
        (groupWidthPx - barGap * (seriesCount - 1)) / seriesCount
    } else groupWidthPx
    val cornerRadiusPx = style.barCornerRadius.toPx()

    when (style.grouping) {
        BarGrouping.GROUPED -> drawGroupedBars(
            data, colors, mapper, style, fractions,
            categoryCount, categoryWidthPx, groupGap, barWidthPx, barGap,
            cornerRadiusPx, theme, textMeasurer,
        )
        BarGrouping.STACKED -> drawStackedBars(
            data, colors, mapper, style, fractions,
            categoryCount, categoryWidthPx, groupGap,
            cornerRadiusPx, theme, textMeasurer,
        )
    }
}

private fun DrawScope.drawGroupedBars(
    data: ChartData,
    colors: List<Color>,
    mapper: ChartCoordinateMapper,
    style: BarChartStyle,
    fractions: List<Float>,
    categoryCount: Int,
    categoryWidthPx: Float,
    groupGap: Float,
    barWidthPx: Float,
    barGap: Float,
    cornerRadiusPx: Float,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
) {
    val seriesCount = data.series.size
    for (catIdx in 0 until categoryCount) {
        for (sIdx in 0 until seriesCount) {
            val series = data.series[sIdx]
            val point = series.points.getOrNull(catIdx) ?: continue
            val color = colors.getOrElse(sIdx) { theme.colors[sIdx % theme.colors.size] }
            val fraction = fractions.getOrElse(sIdx) { fractions.lastOrNull() ?: 1f }

            val groupLeft = mapper.plotLeft + catIdx * categoryWidthPx + groupGap / 2f
            val barLeft = groupLeft + sIdx * (barWidthPx + barGap)

            val topY = mapper.yToPixel(point.y)
            val baseY = mapper.baselineY
            val animatedTop = baseY + (topY - baseY) * fraction

            val top = minOf(animatedTop, baseY)
            val height = kotlin.math.abs(baseY - animatedTop)

            if (style.orientation == BarOrientation.VERTICAL) {
                drawRoundRect(
                    color = color,
                    topLeft = Offset(barLeft, top),
                    size = Size(barWidthPx, height),
                    cornerRadius = CornerRadius(cornerRadiusPx),
                )
                if (style.showValueLabels && fraction >= 0.9f) {
                    drawValueLabel(point.y, barLeft + barWidthPx / 2f, top - 4.dp.toPx(), theme, textMeasurer)
                }
            } else {
                // Horizontal bars
                val baseX = mapper.xToPixel(0f)
                val rightX = mapper.xToPixel(point.y)
                val animatedRight = baseX + (rightX - baseX) * fraction
                val barTop = mapper.plotTop + catIdx * categoryWidthPx + groupGap / 2f + sIdx * (barWidthPx + barGap)
                drawRoundRect(
                    color = color,
                    topLeft = Offset(baseX, barTop),
                    size = Size(animatedRight - baseX, barWidthPx),
                    cornerRadius = CornerRadius(cornerRadiusPx),
                )
            }
        }
    }
}

private fun DrawScope.drawStackedBars(
    data: ChartData,
    colors: List<Color>,
    mapper: ChartCoordinateMapper,
    style: BarChartStyle,
    fractions: List<Float>,
    categoryCount: Int,
    categoryWidthPx: Float,
    groupGap: Float,
    cornerRadiusPx: Float,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
) {
    val seriesCount = data.series.size
    val barWidthPx = categoryWidthPx - groupGap

    for (catIdx in 0 until categoryCount) {
        var stackY = mapper.baselineY
        for (sIdx in 0 until seriesCount) {
            val series = data.series[sIdx]
            val point = series.points.getOrNull(catIdx) ?: continue
            val color = colors.getOrElse(sIdx) { theme.colors[sIdx % theme.colors.size] }
            val fraction = fractions.getOrElse(sIdx) { fractions.lastOrNull() ?: 1f }

            val barLeft = mapper.plotLeft + catIdx * categoryWidthPx + groupGap / 2f
            val segmentHeightFull = mapper.baselineY - mapper.yToPixel(point.y)
            val segmentHeight = segmentHeightFull * fraction

            drawRoundRect(
                color = color,
                topLeft = Offset(barLeft, stackY - segmentHeight),
                size = Size(barWidthPx, segmentHeight),
                cornerRadius = CornerRadius(cornerRadiusPx),
            )
            stackY -= segmentHeight
        }
    }
}

private fun DrawScope.drawValueLabel(
    value: Float,
    centerX: Float,
    topY: Float,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
) {
    val label = if (value == kotlin.math.floor(value.toDouble()).toFloat()) {
        value.toInt().toString()
    } else (kotlin.math.round(value * 10f) / 10f).toString()
    val measured = textMeasurer.measure(label, style = theme.labelTextStyle)
    drawText(
        textLayoutResult = measured,
        topLeft = Offset(centerX - measured.size.width / 2f, topY - measured.size.height),
    )
}
