package io.github.komposeCharts.core.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ChartDataTest {

    @Test
    fun constructor_fromDataPoints() {
        val points = listOf(DataPoint(0f, 1f), DataPoint(1f, 2f))
        val data = ChartData(points, "My Series")

        assertEquals(1, data.series.size)
        assertEquals("My Series", data.series[0].label)
        assertEquals(points, data.series[0].points)
    }

    @Test
    fun constructor_fromFloatValues_autoGeneratesX() {
        val data = ChartData(listOf(10f, 20f, 30f))

        val points = data.series[0].points
        assertEquals(3, points.size)
        assertEquals(0f, points[0].x)
        assertEquals(1f, points[1].x)
        assertEquals(2f, points[2].x)
        assertEquals(10f, points[0].y)
        assertEquals(20f, points[1].y)
        assertEquals(30f, points[2].y)
    }

    @Test
    fun constructor_fromFloatValues_withLabels() {
        val data = ChartData(listOf(1f, 2f), labels = listOf("Jan", "Feb"))

        assertEquals("Jan", data.series[0].points[0].label)
        assertEquals("Feb", data.series[0].points[1].label)
    }

    @Test
    fun constructor_fromFloatValues_withoutLabels_labelsAreNull() {
        val data = ChartData(listOf(1f, 2f))

        assertNull(data.series[0].points[0].label)
        assertNull(data.series[0].points[1].label)
    }

    @Test
    fun isEmpty_emptySeriesList() {
        val data = ChartData(emptyList<DataSeries>())
        assertTrue(data.isEmpty)
    }

    @Test
    fun isEmpty_seriesWithNoPoints() {
        val data = ChartData(listOf(DataSeries("Empty", emptyList())))
        assertTrue(data.isEmpty)
    }

    @Test
    fun isEmpty_seriesWithPoints() {
        val data = ChartData(listOf(DataPoint(0f, 1f)))
        assertFalse(data.isEmpty)
    }

    @Test
    fun defaultSeriesLabel() {
        val data = ChartData(listOf(DataPoint(0f, 1f)))
        assertEquals("Series 1", data.series[0].label)
    }

    @Test
    fun dataPoint_labelIsOptional() {
        val withLabel = DataPoint(0f, 1f, "Jan")
        val noLabel = DataPoint(0f, 1f)

        assertEquals("Jan", withLabel.label)
        assertNull(noLabel.label)
    }

    @Test
    fun dataSeries_colorTokenIsOptional() {
        val autoColor = DataSeries("A", emptyList())
        val explicitColor = DataSeries("B", emptyList(), colorToken = 2)

        assertNull(autoColor.colorToken)
        assertEquals(2, explicitColor.colorToken)
    }
}
