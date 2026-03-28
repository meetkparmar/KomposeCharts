package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.komposeCharts.core.axis.AxisRange
import io.github.komposeCharts.core.data.DataPoint

/**
 * Maps data-space [DataPoint] coordinates to canvas-space [Offset] pixels.
 *
 * Canvas origin is top-left. Y is flipped (data y increases upward, canvas y increases downward).
 *
 * @param canvasSize The pixel dimensions of the drawing area.
 * @param xRange The visible x-axis range.
 * @param yRange The visible y-axis range.
 * @param paddingPx Inset from each canvas edge, leaving room for axis labels.
 */
class ChartCoordinateMapper(
    val canvasSize: Size,
    val xRange: AxisRange,
    val yRange: AxisRange,
    val paddingPx: Float = 0f,
) {
    val plotLeft: Float = paddingPx
    val plotRight: Float = canvasSize.width - paddingPx
    val plotTop: Float = paddingPx
    val plotBottom: Float = canvasSize.height - paddingPx

    val plotWidth: Float = plotRight - plotLeft
    val plotHeight: Float = plotBottom - plotTop

    /** Convert a data x-value to a canvas x pixel. */
    fun xToPixel(dataX: Float): Float =
        plotLeft + xRange.normalize(dataX) * plotWidth

    /** Convert a data y-value to a canvas y pixel (flipped — larger y = higher on screen). */
    fun yToPixel(dataY: Float): Float =
        plotBottom - yRange.normalize(dataY) * plotHeight

    /** Convert a [DataPoint] to canvas [Offset]. */
    fun toOffset(point: DataPoint): Offset =
        Offset(xToPixel(point.x), yToPixel(point.y))

    /** Canvas y-pixel for y=0 (the baseline). */
    val baselineY: Float
        get() = yToPixel(yRange.clamp(0f))
}
