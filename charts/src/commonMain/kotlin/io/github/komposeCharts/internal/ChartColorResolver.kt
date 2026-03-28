package io.github.komposeCharts.internal

import androidx.compose.ui.graphics.Color
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.theme.ChartTheme

/**
 * Resolves the display color for a data series.
 *
 * Priority: explicit [overrideColors] list → series [DataSeries.colorToken] → theme palette (cycled).
 */
internal fun resolveSeriesColor(
    seriesIndex: Int,
    series: DataSeries,
    theme: ChartTheme,
    overrideColors: List<Color>?,
): Color {
    if (overrideColors != null && seriesIndex < overrideColors.size) {
        return overrideColors[seriesIndex]
    }
    val token = series.colorToken ?: seriesIndex
    return if (theme.colors.isEmpty()) Color.Gray
    else theme.colors[token % theme.colors.size]
}
