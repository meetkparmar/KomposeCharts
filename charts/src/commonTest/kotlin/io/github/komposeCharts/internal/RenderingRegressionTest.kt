package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import io.github.komposeCharts.core.axis.AxisRange
import io.github.komposeCharts.core.data.*
import io.github.komposeCharts.style.*
import io.github.komposeCharts.theme.ChartDefaults
import kotlin.test.*

class RenderingRegressionTest {
    private val theme = ChartDefaults.theme()
    private fun data(vararg values: Float) = ChartData(values.mapIndexed { i, v ->
        DataSeries("Series $i", listOf(DataPoint(0f, v)))
    })

    @Test fun stackedGeometryFitsTheComputedAxisInBothOrientations() {
        for (horizontal in listOf(false, true)) {
            val data = data(60f, 70f, -30f, -40f)
            val axes = barAxes(data, BarGrouping.STACKED, 5)
            assertTrue(axes.y.min <= -70f && axes.y.max >= 130f)
            val mapper = if (horizontal) ChartCoordinateMapper(Size(400f, 300f), axes.y, axes.x, 40f)
                else ChartCoordinateMapper(Size(400f, 300f), axes.x, axes.y, 40f)
            val style = BarChartStyle(grouping = BarGrouping.STACKED,
                orientation = if (horizontal) BarOrientation.HORIZONTAL else BarOrientation.VERTICAL)
            val hits = barGeometry(data, mapper, style, List(4) { 1f }, theme, 0f)
            assertEquals(4, hits.size)
            hits.forEach {
                assertTrue(it.bounds.left >= mapper.plotLeft && it.bounds.right <= mapper.plotRight)
                assertTrue(it.bounds.top >= mapper.plotTop && it.bounds.bottom <= mapper.plotBottom)
            }
            if (horizontal) assertEquals(hits[0].bounds.right, hits[1].bounds.left, 0.001f)
            else assertEquals(hits[0].bounds.top, hits[1].bounds.bottom, 0.001f)
        }
    }

    @Test fun legendFilteringPreservesIdentityOverridesAndAnimation() {
        val original = data(60f, 70f)
        val visible = visibleChartData(original, setOf(0))
        val axes = barAxes(visible, BarGrouping.GROUPED, 5)
        val mapper = ChartCoordinateMapper(Size(400f, 300f), axes.x, axes.y, 40f)
        val style = BarChartStyle(barColors = listOf(Color.Red, Color.Blue))
        val hit = barGeometry(visible, mapper, style, listOf(0f, 1f), theme, 0f).single()
        assertEquals(1, hit.seriesIndex)
        assertEquals(Color.Blue, hit.color)
        assertTrue(hit.bounds.height > 0f)
        assertEquals(200f, hit.bounds.center.x, 10f)
        val state = ChartState().apply { hits = listOf(hit) }
        assertEquals(1 to 0, state.select(hit.bounds.center)?.selection)
    }

    @Test fun fractionalAndRepeatedXSelectTheActualPointIndex() {
        val points = listOf(DataPoint(.1f, 1f), DataPoint(.9f, 3f), DataPoint(.9f, 5f))
        val data = ChartData(points = points)
        val mapper = ChartCoordinateMapper(Size(400f, 300f), AxisRange(0f, 1f), AxisRange(0f, 6f))
        val hits = lineGeometry(data, mapper, LineChartStyle(), theme, 5f)
        val state = ChartState().apply { this.hits = hits }
        assertEquals(0 to 1, state.select(mapper.toOffset(points[1]))?.selection)
        assertEquals(0 to 2, state.select(mapper.toOffset(points[2]))?.selection)
    }

    @Test fun barSelectionUsesPositionRatherThanX() {
        val data = ChartData(points = listOf(DataPoint(100f, 10f), DataPoint(900f, 20f)))
        val axes = barAxes(data, BarGrouping.GROUPED, 5)
        val mapper = ChartCoordinateMapper(Size(400f, 300f), axes.x, axes.y, 40f)
        val hits = barGeometry(data, mapper, BarChartStyle(), listOf(1f), theme, 0f)
        val state = ChartState().apply { this.hits = hits }
        assertEquals(0 to 1, state.select(hits[1].bounds.center)?.selection)
    }

    @Test fun missingAndInfinitePointsDoNotShrinkPercentStack() {
        val data = ChartData(listOf(
            DataSeries("valid", listOf(DataPoint(0f, 50f))),
            DataSeries("missing x", listOf(DataPoint(Float.NaN, 50f))),
            DataSeries("missing y", listOf(DataPoint(0f, Float.POSITIVE_INFINITY))),
        ))
        val axes = barAxes(data, BarGrouping.PERCENT_STACKED, 5)
        val mapper = ChartCoordinateMapper(Size(400f, 300f), axes.x, axes.y, 40f)
        val hit = barGeometry(data, mapper, BarChartStyle(grouping = BarGrouping.PERCENT_STACKED), List(3) { 1f }, theme, 0f).single()
        assertEquals(100f, hit.displayValue)
        assertEquals(mapper.plotTop, hit.bounds.top)
        assertEquals(mapper.plotBottom, hit.bounds.bottom)
    }

    @Test fun negativePaletteTokensWrapSafely() {
        for (token in listOf(-1, Int.MIN_VALUE, Int.MAX_VALUE)) {
            val series = DataSeries("test", emptyList(), token)
            assertTrue(resolveSeriesColor(0, series, theme, null) in theme.colors)
        }
        assertEquals(theme.colors.last(), resolveSeriesColor(0, DataSeries("test", emptyList(), -1), theme, null))
    }

    @Test fun extremeAxisMapperHasFiniteCoordinates() {
        val range = AxisRange(-Float.MAX_VALUE, Float.MAX_VALUE)
        val mapper = ChartCoordinateMapper(Size(400f, 300f), range, range)
        assertEquals(Offset(200f, 150f), mapper.toOffset(DataPoint(0f, 0f)))
    }
}
