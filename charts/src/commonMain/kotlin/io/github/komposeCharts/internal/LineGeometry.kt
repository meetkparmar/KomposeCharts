package io.github.komposeCharts.internal

import androidx.compose.ui.geometry.Rect
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.style.LineChartStyle
import io.github.komposeCharts.theme.ChartTheme

internal fun lineGeometry(
    data: ChartData,
    mapper: ChartCoordinateMapper,
    style: LineChartStyle,
    theme: ChartTheme,
    hitRadius: Float,
): List<ChartHit> = data.series.flatMapIndexed { seriesIndex, series ->
    series.points.mapIndexedNotNull { pointIndex, point ->
        if (point.isMissing) null else {
            val center = mapper.toOffset(point)
            ChartHit(
                seriesIndex, pointIndex, point,
                Rect(center.x - hitRadius, center.y - hitRadius, center.x + hitRadius, center.y + hitRadius),
                resolveSeriesColor(seriesIndex, series, theme, style.lineColors),
            )
        }
    }
}
