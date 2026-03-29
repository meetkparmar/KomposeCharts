package io.github.komposeCharts.internal

import androidx.compose.ui.graphics.Color
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.theme.ChartTheme
import kotlin.test.Test
import kotlin.test.assertEquals

class ChartColorResolverTest {

    private val red = Color.Red
    private val green = Color.Green
    private val blue = Color.Blue

    private fun makeTheme(colors: List<Color>) = ChartTheme(colors = colors)

    @Test
    fun overrideColors_takePriorityOverTheme() {
        val theme = makeTheme(listOf(green, blue))
        val series = DataSeries("s", emptyList())
        val result = resolveSeriesColor(0, series, theme, listOf(red))
        assertEquals(red, result)
    }

    @Test
    fun overrideColors_fallsBackToPaletteWhenIndexOutOfBounds() {
        val theme = makeTheme(listOf(green, blue))
        val series = DataSeries("s", emptyList())
        // override has 1 entry, series index 1 is out of bounds → use theme palette
        val result = resolveSeriesColor(1, series, theme, listOf(red))
        assertEquals(blue, result)
    }

    @Test
    fun colorToken_usedWhenNoOverride() {
        val theme = makeTheme(listOf(red, green, blue))
        val series = DataSeries("s", emptyList(), colorToken = 2)
        val result = resolveSeriesColor(0, series, theme, null)
        assertEquals(blue, result)
    }

    @Test
    fun colorToken_cyclesWhenExceedsPaletteSize() {
        val theme = makeTheme(listOf(red, green, blue)) // 3 colors
        val series = DataSeries("s", emptyList(), colorToken = 4) // 4 % 3 = 1 → green
        val result = resolveSeriesColor(0, series, theme, null)
        assertEquals(green, result)
    }

    @Test
    fun nullColorToken_usesSeriesIndex() {
        val theme = makeTheme(listOf(red, green, blue))
        val series = DataSeries("s", emptyList(), colorToken = null)
        val result = resolveSeriesColor(2, series, theme, null)
        assertEquals(blue, result)
    }

    @Test
    fun emptyThemePalette_returnsGray() {
        val theme = makeTheme(emptyList())
        val series = DataSeries("s", emptyList())
        val result = resolveSeriesColor(0, series, theme, null)
        assertEquals(Color.Gray, result)
    }
}
