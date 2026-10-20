package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Rect
import io.github.komposeCharts.core.charts.BarMath
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.BarGrouping
import io.github.komposeCharts.style.BarOrientation
import io.github.komposeCharts.theme.ChartTheme
import kotlin.math.abs
import kotlin.math.max

/** Geometry is shared by drawing and hit testing and can be tested without a Canvas. */
internal fun barGeometry(
    data: ChartData,
    mapper: ChartCoordinateMapper,
    style: BarChartStyle,
    fractions: List<Float>,
    theme: ChartTheme,
    minBarPx: Float,
): List<ChartHit> {
    val indices = data.series.indices.filter { data.series[it].points.isNotEmpty() }
    val categoryCount = data.series.maxOfOrNull { it.points.size } ?: 0
    if (categoryCount == 0 || indices.isEmpty()) return emptyList()
    val layout = BarLayout(mapper, style, categoryCount, indices.size, minBarPx)
    return (0 until categoryCount).flatMap { category ->
        val stack = StackOffsets()
        val total = BarMath.categoryTotal(data, category)
        indices.mapIndexedNotNull { slot, index ->
            val series = data.series[index]
            val point = series.points.getOrNull(category)?.takeUnless { it.isMissing }
            point?.let {
                val value = if (style.grouping == BarGrouping.PERCENT_STACKED) {
                    if (total == 0.0) 0f else (abs(it.y.toDouble()) / total * 100).toFloat()
                } else it.y
                val rect = layout.rectangle(category, slot, value, fractions.getOrElse(index) { 1f }, stack)
                ChartHit(index, category, it, rect, resolveSeriesColor(index, series, theme, style.barColors), value)
            }
        }
    }
}

private class StackOffsets(var positive: Float = 0f, var negative: Float = 0f)

private class BarLayout(
    val mapper: ChartCoordinateMapper,
    val style: BarChartStyle,
    categoryCount: Int,
    seriesCount: Int,
    val minBarPx: Float,
) {
    val vertical = style.orientation == BarOrientation.VERTICAL
    val grouped = style.grouping == BarGrouping.GROUPED
    val band = (if (vertical) mapper.plotWidth else mapper.plotHeight) / categoryCount
    val group = band * (1f - style.groupSpacing)
    val slotWidth = if (grouped) group / seriesCount else group
    val width = if (grouped) slotWidth * (1f - style.barSpacing) else group
    val baseline = if (vertical) mapper.baselineY else mapper.xToPixel(mapper.xRange.clamp(0f))

    fun rectangle(category: Int, slot: Int, value: Float, fraction: Float, stack: StackOffsets): Rect {
        val categoryStart = (if (vertical) mapper.plotLeft else mapper.plotTop) +
            category * band + (band - group) / 2 + if (grouped) slot * slotWidth else 0f
        val target = if (vertical) mapper.yToPixel(value) else mapper.xToPixel(value)
        val length = if (value == 0f) 0f else max(abs(target - baseline) * fraction.coerceIn(0f, 1f), minBarPx)
        val offset = if (grouped) 0f else if (value >= 0f) stack.positive else stack.negative
        val direction = (if (value >= 0f) 1 else -1) * (if (vertical) -1 else 1)
        val start = baseline + direction * offset
        val end = start + direction * length
        if (value >= 0f) stack.positive += length else stack.negative += length
        val lower = if (vertical) mapper.plotTop else mapper.plotLeft
        val upper = if (vertical) mapper.plotBottom else mapper.plotRight
        val from = minOf(start, end).coerceIn(lower, maxOf(lower, upper))
        val to = maxOf(start, end).coerceIn(lower, maxOf(lower, upper))
        return if (vertical) Rect(categoryStart, from, categoryStart + width, to)
            else Rect(from, categoryStart, to, categoryStart + width)
    }
}
