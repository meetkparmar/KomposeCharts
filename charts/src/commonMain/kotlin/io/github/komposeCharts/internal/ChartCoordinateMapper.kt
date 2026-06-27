package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import io.github.komposeCharts.core.axis.AxisRange
import io.github.komposeCharts.core.data.DataPoint

/**
 * Maps data-space coordinates to canvas pixel coordinates.
 *
 * The plot area is the canvas minus [paddingPx] on each side.
 * Y-axis is flipped: higher data values map to lower pixel y (standard screen coordinates).
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

    /** Pixel y-coordinate of the value 0 baseline, clamped to the plot area. */
    val baselineY: Float = yToPixel(yRange.clamp(0f))

    /** Maps a data x-value to a canvas x-pixel. */
    fun xToPixel(dataX: Float): Float {
        val normalized = xRange.normalize(dataX)
        return plotLeft + normalized * plotWidth
    }

    /** Maps a data y-value to a canvas y-pixel (flipped). */
    fun yToPixel(dataY: Float): Float {
        val normalized = yRange.normalize(dataY)
        return plotBottom - normalized * plotHeight
    }

    /** Converts a [DataPoint] to a canvas [Offset]. */
    fun toOffset(point: DataPoint): Offset = Offset(xToPixel(point.x), yToPixel(point.y))
}
