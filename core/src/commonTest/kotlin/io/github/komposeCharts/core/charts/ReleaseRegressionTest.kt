package io.github.komposeCharts.core.charts

import io.github.komposeCharts.core.axis.AxisRange
import io.github.komposeCharts.core.axis.AxisRangeCalculator
import io.github.komposeCharts.core.data.*
import kotlin.test.*

class ReleaseRegressionTest {
    @Test fun narrowLargeRangeHasFiniteDistinctBoundedTicks() {
        val (range, ticks) = AxisRangeCalculator.nice(100000000f, 100000008f)
        assertTrue(range.min <= 100000000f && range.max >= 100000008f)
        assertTrue(ticks.size in 2..1003)
        assertTrue(ticks.all { it.isFinite() })
        assertTrue(ticks.zipWithNext().all { (a, b) -> a < b })
    }

    @Test fun hugeTickRequestIsBounded() {
        val (_, ticks) = AxisRangeCalculator.nice(0f, 1f, Int.MAX_VALUE)
        assertTrue(ticks.size in 2..1003)
        assertEquals(0f, ticks.first())
        assertEquals(1f, ticks.last())
    }

    @Test fun extremeFiniteAxisNormalizesWithoutOverflow() {
        val (range, ticks) = AxisRangeCalculator.nice(-Float.MAX_VALUE, Float.MAX_VALUE)
        assertEquals(0f, range.normalize(-Float.MAX_VALUE))
        assertEquals(0.5f, range.normalize(0f))
        assertEquals(1f, range.normalize(Float.MAX_VALUE))
        assertTrue(ticks.all { it.isFinite() })
    }

    @Test fun constantExtremeAndSubnormalValuesProduceValidBounds() {
        for (value in listOf(Float.MAX_VALUE, -Float.MAX_VALUE, Float.MIN_VALUE, -Float.MIN_VALUE, 0f)) {
            val range = AxisRangeCalculator.fromData(ChartData(values = listOf(value)), false).second
            assertTrue(range.min.isFinite() && range.max.isFinite() && range.min < range.max)
            val (nice, _) = AxisRangeCalculator.nice(value, value)
            assertTrue(nice.min <= value && nice.max >= value)
        }
    }

    @Test fun invalidAxisBoundsAreRejected() {
        assertFailsWith<IllegalArgumentException> { AxisRangeCalculator.nice(Float.NaN, 1f) }
        assertFailsWith<IllegalArgumentException> { AxisRangeCalculator.nice(0f, Float.POSITIVE_INFINITY) }
        assertFailsWith<IllegalArgumentException> { AxisRangeCalculator.nice(2f, 1f) }
        assertFailsWith<IllegalArgumentException> { AxisRange(0f, Float.POSITIVE_INFINITY) }
    }

    @Test fun hugePieValuesStillFillCircle() {
        val slices = PieMath.computeSlices(listOf(Float.MAX_VALUE, Float.MAX_VALUE))
        assertEquals(listOf(0.5f, 0.5f), slices.map { it.fraction })
        assertEquals(360f, slices.sumOf { it.sweepAngle.toDouble() }.toFloat())
    }

    @Test fun pieIgnoresAllNonFiniteValuesWithoutLosingIndices() {
        val slices = PieMath.computeSlices(listOf(Float.POSITIVE_INFINITY, Float.NaN, -1f, 5f))
        assertEquals(listOf(0f, 0f, 0f, 1f), slices.map { it.fraction })
        assertEquals(listOf(0, 1, 2, 3), slices.map { it.index })
    }

    @Test fun stackedRangeIncludesSeparatePositiveAndNegativeTotals() {
        val data = ChartData(listOf(
            DataSeries("A", listOf(DataPoint(0f, 60f), DataPoint(1f, -30f))),
            DataSeries("B", listOf(DataPoint(0f, 70f), DataPoint(1f, -40f))),
        ))
        assertEquals(AxisRange(-70f, 130f), BarMath.stackedRange(data))
    }

    @Test fun percentTotalsExcludeMissingCoordinatesAndDoNotOverflow() {
        val points = listOf(DataPoint(0f, 50f), DataPoint(Float.NaN, 50f), DataPoint(0f, Float.POSITIVE_INFINITY))
        val data = ChartData(points.mapIndexed { i, p -> DataSeries("$i", listOf(p)) })
        assertEquals(50.0, BarMath.categoryTotal(data, 0))
        val huge = ChartData(List(2) { DataSeries("$it", listOf(DataPoint(0f, Float.MAX_VALUE))) })
        assertEquals(Float.MAX_VALUE.toDouble() * 2, BarMath.categoryTotal(huge, 0))
    }
}
