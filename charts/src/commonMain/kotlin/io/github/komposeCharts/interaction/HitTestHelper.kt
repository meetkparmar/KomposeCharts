package io.github.komposeCharts.interaction

import androidx.compose.ui.geometry.Offset
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.internal.ChartCoordinateMapper
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

/**
 * Hit testing utilities for all chart types.
 */
object HitTestHelper {

    /**
     * Returns the (seriesIndex, DataPoint) of the nearest data point to [tapOffset]
     * within [thresholdPx] pixels, or null if nothing is close enough.
     */
    fun nearestDataPoint(
        tapOffset: Offset,
        mapper: ChartCoordinateMapper,
        series: List<DataSeries>,
        thresholdPx: Float = 32f,
    ): Pair<Int, DataPoint>? {
        var bestDist = Float.MAX_VALUE
        var bestResult: Pair<Int, DataPoint>? = null

        series.forEachIndexed { sIdx, s ->
            s.points.forEach { point ->
                val offset = mapper.toOffset(point)
                val dist = distance(tapOffset, offset)
                if (dist < bestDist && dist <= thresholdPx) {
                    bestDist = dist
                    bestResult = sIdx to point
                }
            }
        }
        return bestResult
    }

    /**
     * Returns the (seriesIndex, DataPoint) of the bar that was tapped, or null.
     * Checks all rects described by [barRects].
     */
    fun hitBar(
        tapOffset: Offset,
        barRects: List<Triple<Int, DataPoint, androidx.compose.ui.geometry.Rect>>,
    ): Pair<Int, DataPoint>? {
        return barRects
            .firstOrNull { (_, _, rect) -> rect.contains(tapOffset) }
            ?.let { (sIdx, point, _) -> sIdx to point }
    }

    /**
     * Returns the (index, DataPoint) of the pie slice tapped, given the slice angles.
     *
     * @param sliceAngles List of (startAngle, sweepAngle, DataPoint) tuples.
     * @param center Center of the pie chart.
     * @param outerRadius Outer radius.
     * @param innerRadius Inner radius (0 for full pie).
     */
    fun hitPieSlice(
        tapOffset: Offset,
        sliceAngles: List<Triple<Float, Float, DataPoint>>,
        center: Offset,
        outerRadius: Float,
        innerRadius: Float = 0f,
    ): DataPoint? {
        val dx = tapOffset.x - center.x
        val dy = tapOffset.y - center.y
        val dist = sqrt(dx * dx + dy * dy)

        if (dist > outerRadius || dist < innerRadius) return null

        var angleDeg = (atan2(dy.toDouble(), dx.toDouble()) * (180.0 / PI)).toFloat()
        if (angleDeg < 0) angleDeg += 360f

        for ((startAngle, sweepAngle, point) in sliceAngles) {
            val normalizedStart = ((startAngle % 360f) + 360f) % 360f
            val end = normalizedStart + sweepAngle
            if (angleDeg >= normalizedStart && angleDeg <= end) return point
        }
        return null
    }

    private fun distance(a: Offset, b: Offset): Float {
        val dx = a.x - b.x
        val dy = a.y - b.y
        return sqrt(dx * dx + dy * dy)
    }
}
