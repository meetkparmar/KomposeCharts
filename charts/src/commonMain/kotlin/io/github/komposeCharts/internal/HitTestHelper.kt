package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import io.github.komposeCharts.core.data.DataPoint

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
}
