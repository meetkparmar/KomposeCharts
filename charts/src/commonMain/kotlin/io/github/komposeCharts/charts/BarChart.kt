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
 * A composable bar chart supporting grouped, stacked, and percent-stacked layouts
 * with both vertical and horizontal orientations.
 *
 * Features:
 * - Gradient fills, rounded corners, bar strokes
 * - Entry animation with per-series stagger
 * - Value labels with configurable positioning
 * - Reference/threshold lines
 * - Selection highlighting and crosshair guidelines
 * - Interactive legend (click to toggle series)
 * - Tooltip on tap
 * - Negative value support
 * - Null/gap handling
 * - Min bar height for tiny values
 *
 * @param data The chart data (one or more series).
 * @param modifier Modifier for the chart container.
 * @param style Visual and behavioral configuration.
 * @param onBarClick Callback when a bar is tapped: (seriesIndex, dataPoint).
 */
@Composable
fun BarChart(
    data: ChartData,
    modifier: Modifier = Modifier,
    style: BarChartStyle = BarChartStyle(),
    onBarClick: ((seriesIndex: Int, point: DataPoint) -> Unit)? = null,
) {
    if (data.isEmpty) return
    val theme = LocalChartTheme.current
    val measurer = rememberTextMeasurer()
    val state = rememberChartState(data)
    val visible = remember(data, state.hidden) { visibleChartData(data, state.hidden) }
    val axes = remember(visible, style.axisStyle.yTickCount, style.grouping) { barAxes(visible, style.grouping, style.axisStyle.yTickCount) }
    val fractions = data.series.indices.map { rememberChartAnimationState(style.animation, data, it).fraction }
    val colors = data.series.mapIndexed { index, series -> resolveSeriesColor(index, series, theme, style.barColors) }
    val labels = if (data.series.size > 1) data.series.map { it.label } else emptyList()
    val swapped = style.orientation == BarOrientation.HORIZONTAL && style.swapAxes
    val axisStyle = if (style.grouping == BarGrouping.PERCENT_STACKED && style.axisStyle.yLabelFormatter == null) {
        style.axisStyle.copy(yLabelFormatter = { "${it.toInt()}%" })
    } else style.axisStyle
    val input = Modifier.chartInput(state, style.crosshairStyle) { offset ->
        val hit = state.select(offset)
        if (hit != null) {
            onBarClick?.invoke(hit.seriesIndex, hit.point)
            if (style.tooltipStyle != null) {
                val format = style.valueLabelFormatter ?: ::formatChartValue
                state.tooltip = "${hit.point.label ?: "Item ${hit.pointIndex}"}: ${format(hit.point.y)}"
            }
        }
    }
    ChartFrame(modifier, state, labels, colors, style.legendStyle, style.tooltipStyle, theme) {
        Canvas(Modifier.fillMaxWidth().height(250.dp).padding(theme.contentPadding).then(input)) {
            val mapper = if (swapped) ChartCoordinateMapper(size, axes.y, axes.x, 40f)
                else ChartCoordinateMapper(size, axes.x, axes.y, 40f)
            drawGrid(axes.yTicks, mapper, axisStyle, theme, swapped)
            drawAxes(axes.xTicks, axes.yTicks, mapper, axisStyle, theme, measurer, axes.labels, swapped, axes.xTicks.size)
            state.hits = drawBars(visible, mapper, style, fractions, theme, measurer, state.selection)
            drawReferenceLines(style.referenceLines, mapper, theme, measurer)
            val pointer = state.pointer
            val crosshair = style.crosshairStyle
            if (pointer != null && crosshair != null) drawCrosshair(pointer, mapper, crosshair)
        }
    }
}
