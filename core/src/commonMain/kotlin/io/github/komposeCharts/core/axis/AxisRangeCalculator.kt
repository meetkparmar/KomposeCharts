package io.github.komposeCharts.core.axis

import io.github.komposeCharts.core.data.ChartData
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object AxisRangeCalculator {

    /**
     * Auto-calculates x and y [AxisRange] from all points in [data].
     *
     * @param includeZero If true, y-axis min is at most 0 (bars start from baseline).
     * @return Pair of (xRange, yRange).
     */
    fun fromData(
        data: ChartData,
        includeZero: Boolean = true,
    ): Pair<AxisRange, AxisRange> {
        val allPoints = data.series.flatMap { it.points }
        require(allPoints.isNotEmpty()) { "ChartData must contain at least one point" }

        val xMin = allPoints.minOf { it.x }
        val xMax = allPoints.maxOf { it.x }
        val rawYMin = allPoints.minOf { it.y }
        val rawYMax = allPoints.maxOf { it.y }

        val yMin = if (includeZero) minOf(0f, rawYMin) else rawYMin
        val yMax = rawYMax

        // Ensure ranges are never zero-width (single point edge case)
        val safeXMin = if (xMin == xMax) xMin - 1f else xMin
        val safeXMax = if (xMin == xMax) xMax + 1f else xMax
        val safeYMin = if (yMin == yMax) yMin - 1f else yMin
        val safeYMax = if (yMin == yMax) yMax + 1f else yMax

        return Pair(
            AxisRange(safeXMin, safeXMax),
            AxisRange(safeYMin, safeYMax),
        )
    }

    /**
     * Computes a "nice" axis range that rounds min/max to human-friendly values
     * and returns evenly-spaced tick positions.
     *
     * @param min Raw data minimum.
     * @param max Raw data maximum.
     * @param targetTicks Desired number of ticks (actual count may differ slightly).
     * @return Pair of (niceRange, tickValues).
     */
    fun nice(
        min: Float,
        max: Float,
        targetTicks: Int = 5,
    ): Pair<AxisRange, List<Float>> {
        require(targetTicks >= 2) { "targetTicks must be >= 2" }

        val range = max - min
        if (range == 0f) {
            val niceMin = floor(min.toDouble() - 1.0).toFloat()
            val niceMax = ceil(max.toDouble() + 1.0).toFloat()
            val ticks = (0..targetTicks).map { i ->
                niceMin + i * (niceMax - niceMin) / targetTicks
            }
            return Pair(AxisRange(niceMin, niceMax), ticks)
        }

        val roughStep = range / (targetTicks - 1).toDouble()
        val magnitude = 10.0.pow(floor(log10(roughStep)))
        val normalizedStep = roughStep / magnitude

        val niceStep = when {
            normalizedStep <= 1.0 -> 1.0
            normalizedStep <= 2.0 -> 2.0
            normalizedStep <= 5.0 -> 5.0
            else -> 10.0
        } * magnitude

        val niceMin = (floor(min / niceStep) * niceStep).toFloat()
        val niceMax = (ceil(max / niceStep) * niceStep).toFloat()

        val ticks = mutableListOf<Float>()
        var tick = niceMin
        while (tick <= niceMax + abs(niceMax) * 1e-6) {
            ticks.add(tick)
            tick = (tick + niceStep).toFloat()
        }

        return Pair(AxisRange(niceMin, niceMax), ticks)
    }
}
