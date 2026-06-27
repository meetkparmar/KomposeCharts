package io.github.komposeCharts.core.charts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PieMathTest {

    @Test
    fun computeSlices_sweepsSumTo360() {
        val slices = PieMath.computeSlices(listOf(1f, 2f, 3f, 4f))
        val totalSweep = slices.sumOf { it.sweepAngle.toDouble() }.toFloat()
        assertEquals(360f, totalSweep, absoluteTolerance = 0.01f)
    }

    @Test
    fun computeSlices_fractionsMatchValues() {
        val slices = PieMath.computeSlices(listOf(25f, 25f, 50f))
        assertEquals(0.25f, slices[0].fraction, absoluteTolerance = 0.001f)
        assertEquals(0.25f, slices[1].fraction, absoluteTolerance = 0.001f)
        assertEquals(0.50f, slices[2].fraction, absoluteTolerance = 0.001f)
    }

    @Test
    fun computeSlices_startAnglesAreCumulative() {
        val slices = PieMath.computeSlices(listOf(1f, 1f, 2f), startAngle = -90f)
        assertEquals(-90f, slices[0].startAngle, absoluteTolerance = 0.01f)
        // First slice (25%) sweeps 90°, so second starts at 0°.
        assertEquals(0f, slices[1].startAngle, absoluteTolerance = 0.01f)
        // Second slice (25%) sweeps 90°, so third starts at 90°.
        assertEquals(90f, slices[2].startAngle, absoluteTolerance = 0.01f)
    }

    @Test
    fun computeSlices_preservesIndexAlignment() {
        val slices = PieMath.computeSlices(listOf(10f, 0f, 30f))
        assertEquals(3, slices.size)
        assertEquals(1, slices[1].index)
        assertEquals(0f, slices[1].sweepAngle, absoluteTolerance = 0.01f)
    }

    @Test
    fun computeSlices_negativeAndNaNTreatedAsZero() {
        val slices = PieMath.computeSlices(listOf(50f, -20f, Float.NaN))
        assertEquals(0f, slices[1].value, absoluteTolerance = 0.01f)
        assertEquals(0f, slices[2].value, absoluteTolerance = 0.01f)
        // Only the first slice has value, so it fills the circle.
        assertEquals(360f, slices[0].sweepAngle, absoluteTolerance = 0.01f)
    }

    @Test
    fun computeSlices_emptyWhenTotalNonPositive() {
        assertTrue(PieMath.computeSlices(listOf(0f, 0f)).isEmpty())
        assertTrue(PieMath.computeSlices(emptyList()).isEmpty())
    }
}

private fun assertEquals(expected: Float, actual: Float, absoluteTolerance: Float) {
    assertTrue(
        kotlin.math.abs(expected - actual) <= absoluteTolerance,
        "Expected $expected but was $actual (tolerance ±$absoluteTolerance)",
    )
}
