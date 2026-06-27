package io.github.komposeCharts.internal

import androidx.compose.ui.graphics.Color
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.theme.ChartTheme

/**
 * Resolves the display color for a data series.
 *
 * Priority order:
 * 1. [overrideColors] at [seriesIndex] (if present and in bounds)
 * 2. [series.colorToken] mapped into the theme palette
 * 3. Theme palette at [seriesIndex] (cycled)
 * 4. [Color.Gray] fallback if the palette is empty
 */
internal fun resolveSeriesColor(
    seriesIndex: Int,
    series: DataSeries,
    theme: ChartTheme,
    overrideColors: List<Color>?,
): Color {
    // 1. Override colors
    overrideColors?.getOrNull(seriesIndex)?.let { return it }

    val palette = theme.colors
    if (palette.isEmpty()) return Color.Gray

    // 2. Explicit color token
    series.colorToken?.let { token ->
        return palette[token % palette.size]
    }

    // 3. Theme palette by series index
    return palette[seriesIndex % palette.size]
}
