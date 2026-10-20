package io.github.komposeCharts.core.charts

import io.github.komposeCharts.core.axis.AxisRange
import io.github.komposeCharts.core.data.ChartData
import kotlin.math.abs

/** Shared validity and range calculations for categorical stacks. */
object BarMath {
    fun categoryTotal(data: ChartData, category: Int): Double = data.series.sumOf { series ->
        val point = series.points.getOrNull(category)
        if (point == null || point.isMissing) 0.0 else abs(point.y.toDouble())
    }

    fun stackedRange(data: ChartData): AxisRange {
        var min = 0.0
        var max = 0.0
        val categoryCount = data.series.maxOfOrNull { it.points.size } ?: 0
        for (category in 0 until categoryCount) {
            val values = data.series.mapNotNull { it.points.getOrNull(category) }.filterNot { it.isMissing }
            min = minOf(min, values.sumOf { minOf(0.0, it.y.toDouble()) })
            max = maxOf(max, values.sumOf { maxOf(0.0, it.y.toDouble()) })
        }
        if (min == max) return AxisRange(0f, 1f)
        val limit = Float.MAX_VALUE.toDouble()
        return AxisRange(min.coerceAtLeast(-limit).toFloat(), max.coerceAtMost(limit).toFloat())
    }
}
