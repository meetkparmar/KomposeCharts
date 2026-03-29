package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Size
import io.github.komposeCharts.core.axis.AxisRange
import io.github.komposeCharts.core.data.DataPoint
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ChartCoordinateMapperTest {

    private val canvas = Size(200f, 100f)
    private val xRange = AxisRange(0f, 10f)
    private val yRange = AxisRange(0f, 10f)
    private val padding = 10f

    private fun mapper(paddingPx: Float = padding) =
        ChartCoordinateMapper(canvas, xRange, yRange, paddingPx)

    @Test
    fun xToPixel_minMapsToPlotLeft() {
        val m = mapper()
        assertEquals(m.plotLeft, m.xToPixel(xRange.min), absoluteTolerance = 0.01f)
    }

    @Test
    fun xToPixel_maxMapsToPlotRight() {
        val m = mapper()
        assertEquals(m.plotRight, m.xToPixel(xRange.max), absoluteTolerance = 0.01f)
    }

    @Test
    fun yToPixel_minMapsToPlotBottom() {
        val m = mapper()
        assertEquals(m.plotBottom, m.yToPixel(yRange.min), absoluteTolerance = 0.01f)
    }

    @Test
    fun yToPixel_maxMapsToPlotTop() {
        val m = mapper()
        assertEquals(m.plotTop, m.yToPixel(yRange.max), absoluteTolerance = 0.01f)
    }

    @Test
    fun baselineY_atZero_whenZeroInRange() {
        // yRange is 0..10, so y=0 is at plotBottom
        val m = ChartCoordinateMapper(canvas, xRange, AxisRange(-5f, 5f), padding)
        // y=0 is at the midpoint of the range, so it maps to the center of plotHeight
        val expected = m.plotBottom - m.yRange.normalize(0f) * m.plotHeight
        assertEquals(expected, m.baselineY, absoluteTolerance = 0.01f)
    }

    @Test
    fun baselineY_clampedToPlotBottom_whenRangeAllPositive() {
        val m = ChartCoordinateMapper(canvas, xRange, AxisRange(5f, 20f), padding)
        // y=0 is below the range; clamp(0f) = 5f (min), which maps to plotBottom
        assertEquals(m.plotBottom, m.baselineY, absoluteTolerance = 0.01f)
    }

    @Test
    fun toOffset_matchesXYToPixelCombined() {
        val m = mapper()
        val point = DataPoint(5f, 5f)
        val offset = m.toOffset(point)
        assertEquals(m.xToPixel(5f), offset.x, absoluteTolerance = 0.01f)
        assertEquals(m.yToPixel(5f), offset.y, absoluteTolerance = 0.01f)
    }

    @Test
    fun zeroPadding_plotFillsEntireCanvas() {
        val m = ChartCoordinateMapper(canvas, xRange, yRange, paddingPx = 0f)
        assertEquals(0f, m.plotLeft)
        assertEquals(canvas.width, m.plotRight)
        assertEquals(0f, m.plotTop)
        assertEquals(canvas.height, m.plotBottom)
    }

    @Test
    fun plotDimensions_accountForPadding() {
        val m = mapper(paddingPx = 10f)
        assertEquals(canvas.width - 2 * 10f, m.plotWidth, absoluteTolerance = 0.01f)
        assertEquals(canvas.height - 2 * 10f, m.plotHeight, absoluteTolerance = 0.01f)
    }
}

private fun assertEquals(expected: Float, actual: Float, absoluteTolerance: Float) {
    assertTrue(
        kotlin.math.abs(expected - actual) <= absoluteTolerance,
        "Expected $expected but was $actual (tolerance ±$absoluteTolerance)"
    )
}
