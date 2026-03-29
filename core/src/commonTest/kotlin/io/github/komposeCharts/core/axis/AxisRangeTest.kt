package io.github.komposeCharts.core.axis

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class AxisRangeTest {

    @Test
    fun normalize_atMin_returnsZero() {
        val range = AxisRange(0f, 100f)
        assertEquals(0f, range.normalize(0f))
    }

    @Test
    fun normalize_atMax_returnsOne() {
        val range = AxisRange(0f, 100f)
        assertEquals(1f, range.normalize(100f))
    }

    @Test
    fun normalize_atMidpoint_returnsHalf() {
        val range = AxisRange(0f, 100f)
        assertEquals(0.5f, range.normalize(50f))
    }

    @Test
    fun normalize_belowMin_returnsNegative() {
        val range = AxisRange(0f, 100f)
        assertTrue(range.normalize(-10f) < 0f)
    }

    @Test
    fun normalize_aboveMax_returnsGreaterThanOne() {
        val range = AxisRange(0f, 100f)
        assertTrue(range.normalize(110f) > 1f)
    }

    @Test
    fun clamp_belowMin_returnsMin() {
        val range = AxisRange(0f, 10f)
        assertEquals(0f, range.clamp(-5f))
    }

    @Test
    fun clamp_aboveMax_returnsMax() {
        val range = AxisRange(0f, 10f)
        assertEquals(10f, range.clamp(20f))
    }

    @Test
    fun clamp_withinRange_returnsValue() {
        val range = AxisRange(0f, 10f)
        assertEquals(5f, range.clamp(5f))
    }

    @Test
    fun span_equalsMaxMinusMin() {
        val range = AxisRange(10f, 30f)
        assertEquals(20f, range.span)
    }

    @Test
    fun constructor_throwsWhenMaxEqualsMin() {
        assertFailsWith<IllegalArgumentException> {
            AxisRange(5f, 5f)
        }
    }

    @Test
    fun constructor_throwsWhenMaxLessThanMin() {
        assertFailsWith<IllegalArgumentException> {
            AxisRange(10f, 5f)
        }
    }
}
