package io.github.komposeCharts.core.axis

import io.github.komposeCharts.core.data.ChartData
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

/**
 * Utilities for computing axis ranges and tick positions from chart data.
 */
object AxisRangeCalculator {

    /**
     * Computes x and y [AxisRange]s that encompass all data points in [data].
     *
     * @param includeZero When true (the default), the y-range is extended to include 0.
     *                    This is typical for bar charts where bars grow from a zero baseline.
     * @return A pair of (xRange, yRange).
     */
    fun fromData(data: ChartData, includeZero: Boolean = true): Pair<AxisRange, AxisRange> {
        val allPoints = data.series.flatMap { it.points }.filter { !it.isMissing }

        if (allPoints.isEmpty()) {
            return AxisRange(0f, 1f) to AxisRange(0f, 1f)
        }

        var xMin = Float.MAX_VALUE
        var xMax = -Float.MAX_VALUE
        var yMin = Float.MAX_VALUE
        var yMax = -Float.MAX_VALUE

        for (p in allPoints) {
            if (p.x < xMin) xMin = p.x
            if (p.x > xMax) xMax = p.x
            if (p.y < yMin) yMin = p.y
            if (p.y > yMax) yMax = p.y
        }

        if (includeZero) {
            if (yMin > 0f) yMin = 0f
            if (yMax < 0f) yMax = 0f
        }

        // Handle single-point or all-same-value edge cases
        if (xMin == xMax) {
            val padding = if (xMin == 0f) 1f else abs(xMin) * 0.1f
            xMin -= padding
            xMax += padding
        }
        if (yMin == yMax) {
            val padding = if (yMin == 0f) 1f else abs(yMin) * 0.1f
            yMin -= padding
            yMax += padding
        }

        return AxisRange(xMin, xMax) to AxisRange(yMin, yMax)
    }

    /**
     * Rounds [min] and [max] to human-friendly ("nice") values and generates evenly-spaced tick positions.
     *
     * Uses the 1 / 2 / 5 magnitude rule for nice numbers.
     *
     * @param targetTicks Desired number of ticks (the actual count may differ slightly).
     * @return A pair of (niceRange, tickPositions).
     */
    fun nice(min: Float, max: Float, targetTicks: Int = 5): Pair<AxisRange, List<Float>> {
        if (min == max) {
            val padding = if (min == 0f) 1f else abs(min) * 0.1f
            return nice(min - padding, max + padding, targetTicks)
        }

        val rawRange = max - min
        val roughStep = rawRange / targetTicks.coerceAtLeast(1)
        val niceStep = niceNumber(roughStep, round = true)

        val niceMin = floor(min / niceStep) * niceStep
        val niceMax = ceil(max / niceStep) * niceStep

        val ticks = mutableListOf<Float>()
        var tick = niceMin
        while (tick <= niceMax + niceStep * 0.01f) {
            ticks.add(tick)
            tick += niceStep
        }

        return AxisRange(niceMin, niceMax) to ticks
    }

    /**
     * Returns a "nice" number that is approximately equal to [value].
     * A nice number is 1, 2, or 5 multiplied by a power of 10.
     */
    private fun niceNumber(value: Float, round: Boolean): Float {
        val absValue = abs(value)
        if (absValue < 1e-10f) return 1f

        val exponent = floor(log10(absValue))
        val fraction = absValue / (10f.pow(exponent))

        val niceFraction = if (round) {
            when {
                fraction < 1.5f -> 1f
                fraction < 3f -> 2f
                fraction < 7f -> 5f
                else -> 10f
            }
        } else {
            when {
                fraction <= 1f -> 1f
                fraction <= 2f -> 2f
                fraction <= 5f -> 5f
                else -> 10f
            }
        }

        return niceFraction * (10f.pow(exponent))
    }
}
