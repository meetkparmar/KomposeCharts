package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.renderer.SliceArc
import kotlin.math.atan2
import kotlin.math.hypot

/**
 * Utilities for detecting which chart element was tapped.
 */
object HitTestHelper {

    /**
     * Finds the bar that contains [tapOffset], if any.
     *
     * @param tapOffset The pointer position in canvas coordinates.
     * @param barRects List of (seriesIndex, dataPoint, boundingRect) for each rendered bar.
     * @return The (seriesIndex, dataPoint) of the hit bar, or null if nothing was tapped.
     */
    fun hitBar(
        tapOffset: Offset,
        barRects: List<Triple<Int, DataPoint, Rect>>,
    ): Pair<Int, DataPoint>? {
        for ((seriesIndex, point, rect) in barRects) {
            if (rect.contains(tapOffset)) {
                return seriesIndex to point
            }
        }
        return null
    }

    /**
     * Finds the pie/donut slice that contains [tapOffset], if any.
     *
     * Matches by polar coordinates: the tap distance from the slice center must
     * fall within `[innerRadius, outerRadius]`, and the tap angle must lie within
     * the slice's angular extent (handling wrap-around).
     *
     * @return The (sliceIndex, dataPoint) of the hit slice, or null.
     */
    internal fun hitSlice(
        tapOffset: Offset,
        slices: List<SliceArc>,
    ): Pair<Int, DataPoint>? {
        for (slice in slices) {
            val dx = tapOffset.x - slice.center.x
            val dy = tapOffset.y - slice.center.y
            val dist = hypot(dx, dy)
            if (dist < slice.innerRadius || dist > slice.outerRadius) continue

            // atan2(dy, dx) in degrees matches drawArc's convention (y is down).
            val angle = atan2(dy, dx) * (180f / kotlin.math.PI.toFloat())
            var delta = (angle - slice.startAngle) % 360f
            if (delta < 0f) delta += 360f
            if (delta < slice.sweepAngle) {
                return slice.index to slice.point
            }
        }
        return null
    }
}
