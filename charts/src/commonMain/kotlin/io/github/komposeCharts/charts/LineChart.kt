package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.internal.*
import io.github.komposeCharts.renderer.*
import io.github.komposeCharts.style.*
import io.github.komposeCharts.theme.LocalChartTheme

/**
 * A composable line chart supporting multiple series, optional point markers,
 * area fill, and smooth (curved) interpolation.
 *
 * Features:
 * - Multi-series lines with theme-driven colors
 * - Left-to-right reveal animation with per-series stagger
 * - Optional markers (circle / square / diamond) and area fill
 * - Reference/threshold lines
 * - Crosshair guidelines and selection highlight
 * - Interactive legend (click to toggle series)
 * - Tooltip on tap
 * - Null/gap handling (missing points break the line)
 *
 * @param data The chart data (one or more series). X is a continuous value.
 * @param modifier Modifier for the chart container.
 * @param style Visual and behavioral configuration.
 * @param onPointClick Callback when a point is tapped: (seriesIndex, dataPoint).
 */
@Composable
fun LineChart(
    data: ChartData,
    modifier: Modifier = Modifier,
    style: LineChartStyle = LineChartStyle(),
    onPointClick: ((seriesIndex: Int, point: DataPoint) -> Unit)? = null,
) {
    if (data.isEmpty) return
    val theme = LocalChartTheme.current
    val measurer = rememberTextMeasurer()
    val state = rememberChartState(data)
    val visible = remember(data, state.hidden) { visibleChartData(data, state.hidden) }
    val axes = remember(visible, style.axisStyle.yTickCount) { lineAxes(visible, style.axisStyle.yTickCount) }
    val fractions = data.series.indices.map { rememberChartAnimationState(style.animation, data, it).fraction }
    val colors = data.series.mapIndexed { index, series -> resolveSeriesColor(index, series, theme, style.lineColors) }
    val labels = if (data.series.size > 1) data.series.map { it.label } else emptyList()
    val axisStyle = style.axisStyle
    val swapped = false
    val input = Modifier.chartInput(state, style.crosshairStyle) { offset ->
        val hit = state.select(offset)
        if (hit != null) {
            onPointClick?.invoke(hit.seriesIndex, hit.point)
            if (style.tooltipStyle != null) {
                val format = ::formatChartValue
                state.tooltip = "${hit.point.label ?: "Point ${hit.pointIndex}"}: ${format(hit.point.y)}"
            }
        }
    }
    ChartFrame(modifier, state, labels, colors, style.legendStyle, style.tooltipStyle, theme) {
        Canvas(Modifier.fillMaxWidth().height(250.dp).padding(theme.contentPadding).then(input)) {
            val mapper = if (swapped) ChartCoordinateMapper(size, axes.y, axes.x, 40f)
                else ChartCoordinateMapper(size, axes.x, axes.y, 40f)
            drawGrid(axes.yTicks, mapper, axisStyle, theme, swapped)
            drawAxes(axes.xTicks, axes.yTicks, mapper, axisStyle, theme, measurer, axes.labels, swapped, axes.xTicks.size)
            state.hits = drawLines(visible, mapper, style, fractions, theme, state.selection)
            drawReferenceLines(style.referenceLines, mapper, theme, measurer)
            val pointer = state.pointer
            val crosshair = style.crosshairStyle
            if (pointer != null && crosshair != null) drawCrosshair(pointer, mapper, crosshair)
        }
    }
}
