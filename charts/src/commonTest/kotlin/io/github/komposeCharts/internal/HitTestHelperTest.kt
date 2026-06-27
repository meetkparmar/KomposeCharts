package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Offset
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.renderer.SliceArc
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class HitTestHelperTest {

    private val center = Offset(100f, 100f)

    // Right half (−90°→+90°) and left half (+90°→+270°) of a circle, radius 50.
    private val rightHalf = SliceArc(
        index = 0, point = DataPoint(0f, 1f, "Right"),
        startAngle = -90f, sweepAngle = 180f,
        center = center, outerRadius = 50f, innerRadius = 0f,
    )
    private val leftHalf = SliceArc(
        index = 1, point = DataPoint(1f, 1f, "Left"),
        startAngle = 90f, sweepAngle = 180f,
        center = center, outerRadius = 50f, innerRadius = 0f,
    )
    private val slices = listOf(rightHalf, leftHalf)

    @Test
    fun hitSlice_picksRightHalf() {
        val hit = HitTestHelper.hitSlice(Offset(130f, 100f), slices)
        assertEquals(0, hit?.first)
        assertEquals("Right", hit?.second?.label)
    }

    @Test
    fun hitSlice_picksLeftHalf() {
        val hit = HitTestHelper.hitSlice(Offset(70f, 100f), slices)
        assertEquals(1, hit?.first)
        assertEquals("Left", hit?.second?.label)
    }

    @Test
    fun hitSlice_nullWhenBeyondOuterRadius() {
        // 80px from center, outside the 50px radius.
        assertNull(HitTestHelper.hitSlice(Offset(180f, 100f), slices))
    }

    @Test
    fun hitSlice_nullInsideDonutHole() {
        val donut = listOf(
            rightHalf.copy(innerRadius = 25f),
            leftHalf.copy(innerRadius = 25f),
        )
        // 10px from center is inside the 25px hole.
        assertNull(HitTestHelper.hitSlice(Offset(110f, 100f), donut))
    }
}
