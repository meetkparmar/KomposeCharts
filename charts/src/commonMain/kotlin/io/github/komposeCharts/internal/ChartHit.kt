package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import io.github.komposeCharts.core.data.DataPoint

/** Original series/point indices, independent of x values and legend filtering. */
internal data class ChartHit(
    val seriesIndex: Int,
    val pointIndex: Int,
    val point: DataPoint,
    val bounds: Rect,
    val color: Color,
    val displayValue: Float = point.y,
) {
    val selection: Pair<Int, Int> get() = seriesIndex to pointIndex
}
