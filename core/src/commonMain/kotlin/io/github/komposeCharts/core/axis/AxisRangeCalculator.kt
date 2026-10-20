package io.github.komposeCharts.core.axis

import io.github.komposeCharts.core.data.ChartData
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/** Finite axis bounds and bounded, human-friendly tick generation. */
object AxisRangeCalculator {
    // Bound work even when a caller supplies an extreme tick count.
    private const val MaxTickIntervals = 1000
    private val floatLimit = Float.MAX_VALUE.toDouble()

    fun fromData(data: ChartData, includeZero: Boolean = true): Pair<AxisRange, AxisRange> {
        val points = data.series.flatMap { it.points }.filterNot { it.isMissing }
        if (points.isEmpty()) return AxisRange(0f, 1f) to AxisRange(0f, 1f)
        val x = finiteRange(points.minOf { it.x }, points.maxOf { it.x })
        val minY = points.minOf { it.y }
        val maxY = points.maxOf { it.y }
        val y = finiteRange(if (includeZero) minOf(0f, minY) else minY, if (includeZero) maxOf(0f, maxY) else maxY)
        return x to y
    }

    /**
     * Generates at most 1003 distinct ticks, including both bounds.
     * Double intermediates avoid Float overflow and non-advancing tick loops.
     * Bounds are clipped to finite Float values; invalid/reversed inputs are rejected.
     */
    fun nice(min: Float, max: Float, targetTicks: Int = 5): Pair<AxisRange, List<Float>> {
        val input = finiteRange(min, max)
        val lower = input.min.toDouble()
        val upper = input.max.toDouble()
        val step = niceNumber((upper - lower) / targetTicks.coerceIn(1, MaxTickIntervals))
        val start = (floor(lower / step) * step).coerceAtLeast(-floatLimit)
        val end = (ceil(upper / step) * step).coerceAtMost(floatLimit)
        val range = AxisRange(minOf(input.min, start.toFloat()), maxOf(input.max, end.toFloat()))
        val intervals = ceil((end - start) / step).toInt().coerceIn(1, MaxTickIntervals + 1)
        val ticks = (0..intervals).map { index ->
            (start + index * step).coerceIn(start, end).toFloat()
        }.plus(range.max).distinct()
        return range to ticks
    }

    private fun finiteRange(min: Float, max: Float): AxisRange {
        require(min.isFinite() && max.isFinite() && max >= min) { "Axis bounds must be finite and ordered" }
        if (min < max) return AxisRange(min, max)
        val padding = maxOf(abs(min.toDouble()) * 0.1, Float.MIN_VALUE.toDouble())
        val delta = if (min == 0f) 1.0 else padding
        return AxisRange(
            (min.toDouble() - delta).coerceAtLeast(-floatLimit).toFloat(),
            (max.toDouble() + delta).coerceAtMost(floatLimit).toFloat(),
        )
    }

    private fun niceNumber(value: Double): Double {
        val magnitude = 10.0.pow(floor(log10(value)))
        val fraction = value / magnitude
        val factor = when {
            fraction < 1.5 -> 1.0
            fraction < 3.0 -> 2.0
            fraction < 7.0 -> 5.0
            else -> 10.0
        }
        return factor * magnitude
    }
}
