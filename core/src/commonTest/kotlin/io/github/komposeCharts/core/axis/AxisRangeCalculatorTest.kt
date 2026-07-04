package io.github.komposeCharts.core.axis

import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AxisRangeCalculatorTest {

    @Test
    fun fromData_basicRange() {
        val data = ChartData(listOf(DataPoint(0f, 10f), DataPoint(1f, 50f), DataPoint(2f, 30f)))
        val (xRange, yRange) = AxisRangeCalculator.fromData(data)

        assertEquals(0f, xRange.min)
        assertEquals(2f, xRange.max)
        assertEquals(0f, yRange.min)   // includeZero = true by default
        assertEquals(50f, yRange.max)
    }

    @Test
    fun fromData_includeZeroFalse() {
        val data = ChartData(listOf(DataPoint(0f, 20f), DataPoint(1f, 80f)))
        val (_, yRange) = AxisRangeCalculator.fromData(data, includeZero = false)

        assertEquals(20f, yRange.min)
        assertEquals(80f, yRange.max)
    }

    @Test
    fun fromData_negativeValues() {
        val data = ChartData(listOf(DataPoint(0f, -10f), DataPoint(1f, 5f)))
        val (_, yRange) = AxisRangeCalculator.fromData(data)

        assertEquals(-10f, yRange.min)
        assertEquals(5f, yRange.max)
    }

    @Test
    fun fromData_singlePoint_doesNotThrow() {
        val data = ChartData(listOf(DataPoint(5f, 5f)))
        val (xRange, yRange) = AxisRangeCalculator.fromData(data)

        assertTrue(xRange.max > xRange.min)
        assertTrue(yRange.max > yRange.min)
    }

    @Test
    fun fromData_multiSeries() {
        val data = ChartData(
            listOf(
                DataSeries("A", listOf(DataPoint(0f, 10f), DataPoint(1f, 20f))),
                DataSeries("B", listOf(DataPoint(0f, 5f), DataPoint(1f, 30f))),
            )
        )
        val (_, yRange) = AxisRangeCalculator.fromData(data)

        assertEquals(0f, yRange.min)
        assertEquals(30f, yRange.max)
    }

    @Test
    fun nice_roundsToHumanFriendlyValues() {
        val (range, ticks) = AxisRangeCalculator.nice(0f, 95f, targetTicks = 5)

        assertEquals(0f, range.min)
        assertEquals(100f, range.max)
        assertTrue(ticks.size >= 2)
        // All ticks should be within range
        ticks.forEach { tick -> assertTrue(tick >= range.min && tick <= range.max) }
    }

    @Test
    fun nice_smallValues() {
        val (range, ticks) = AxisRangeCalculator.nice(0f, 1f, targetTicks = 5)

        assertTrue(range.max > range.min)
        assertTrue(ticks.size >= 2)
    }

    @Test
    fun nice_largeValues() {
        val (range, ticks) = AxisRangeCalculator.nice(1000f, 9500f, targetTicks = 5)

        // niceStep=5000: floor(1000/5000)*5000=0, ceil(9500/5000)*5000=10000
        assertEquals(0f, range.min)
        assertEquals(10000f, range.max)
        assertTrue(ticks.isNotEmpty())
    }

    @Test
    fun fromData_allNegativeValues_producesValidRange() {
        val data = ChartData(listOf(DataPoint(0f, -50f), DataPoint(1f, -10f)))
        val (_, yRange) = AxisRangeCalculator.fromData(data, includeZero = true)

        // includeZero=true with all-negative values: yMin = rawMin, yMax = 0
        // Both directions are represented
        assertTrue(yRange.max > yRange.min)
    }

    @Test
    fun fromData_allSameValues_doesNotThrow() {
        val data = ChartData(listOf(DataPoint(0f, 42f), DataPoint(1f, 42f)))
        val (_, yRange) = AxisRangeCalculator.fromData(data)

        // All same values: range should be expanded so max > min
        assertTrue(yRange.max > yRange.min)
    }

    @Test
    fun nice_targetTicksTwo_returnsFewTicks() {
        val (range, ticks) = AxisRangeCalculator.nice(0f, 100f, targetTicks = 2)

        assertTrue(range.max > range.min)
        assertTrue(ticks.size >= 2)
        ticks.forEach { tick -> assertTrue(tick >= range.min && tick <= range.max) }
    }

    @Test
    fun nice_verySmallRange_doesNotThrow() {
        val (range, ticks) = AxisRangeCalculator.nice(0f, 0.001f, targetTicks = 5)

        assertTrue(range.max > range.min)
        assertTrue(ticks.isNotEmpty())
    }

    @Test
    fun fromData_nanX_isIgnored_doesNotThrow() {
        // A point with NaN x used to slip past the missing-filter and produce
        // an invalid AxisRange(MAX, -MAX) that threw in the init block.
        val data = ChartData(
            listOf(DataPoint(Float.NaN, 5f), DataPoint(1f, 10f), DataPoint(2f, 20f))
        )
        val (xRange, yRange) = AxisRangeCalculator.fromData(data)

        assertEquals(1f, xRange.min)
        assertEquals(2f, xRange.max)
        assertTrue(yRange.max > yRange.min)
    }

    @Test
    fun fromData_infiniteValues_areIgnored() {
        val data = ChartData(
            listOf(
                DataPoint(Float.POSITIVE_INFINITY, 1f),
                DataPoint(0f, Float.NEGATIVE_INFINITY),
                DataPoint(1f, 10f),
                DataPoint(2f, 30f),
            )
        )
        val (xRange, yRange) = AxisRangeCalculator.fromData(data)

        assertEquals(1f, xRange.min)
        assertEquals(2f, xRange.max)
        assertTrue(yRange.max.isFinite() && yRange.min.isFinite())
        assertEquals(30f, yRange.max)
    }

    @Test
    fun fromData_allNonFinite_returnsDefaultRange() {
        val data = ChartData(
            listOf(DataPoint(Float.NaN, Float.NaN), DataPoint(0f, Float.POSITIVE_INFINITY))
        )
        val (xRange, yRange) = AxisRangeCalculator.fromData(data)

        assertEquals(0f, xRange.min)
        assertEquals(1f, xRange.max)
        assertEquals(0f, yRange.min)
        assertEquals(1f, yRange.max)
    }
}
