package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.animation.rememberChartAnimationState
import io.github.komposeCharts.core.axis.AxisRangeCalculator
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.interaction.HitTestHelper
import io.github.komposeCharts.interaction.rememberTooltipState
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.internal.resolveSeriesColor
import io.github.komposeCharts.renderer.ChartLegend
import io.github.komposeCharts.renderer.ChartTooltip
import io.github.komposeCharts.renderer.drawAxes
import io.github.komposeCharts.renderer.drawGrid
import io.github.komposeCharts.renderer.drawLineSeries
import io.github.komposeCharts.style.LegendOrientation
import io.github.komposeCharts.style.LegendPosition
import io.github.komposeCharts.style.LineChartStyle
import io.github.komposeCharts.theme.LocalChartTheme

/**
 * Renders a line chart from [data].
 *
 * Supports single and multi-series data, straight/bezier/catmull-rom curves, optional
 * area fill, data point markers, and an animated draw-in effect.
 *
 * @param data The chart data to render.
 * @param modifier Layout modifier for sizing the chart.
 * @param style Visual and animation configuration.
 * @param onDataPointClick Optional callback fired when a data point is tapped.
 */
@Composable
fun LineChart(
    data: ChartData,
    modifier: Modifier = Modifier,
    style: LineChartStyle = LineChartStyle(),
    onDataPointClick: ((seriesIndex: Int, point: DataPoint) -> Unit)? = null,
) {
    if (data.isEmpty) return

    val theme = LocalChartTheme.current
    val textMeasurer = rememberTextMeasurer()
    val dataKey = remember(data) { data.hashCode() }

    val seriesColors = data.series.mapIndexed { i, s ->
        resolveSeriesColor(i, s, theme, style.lineColors)
    }

    val animStates = data.series.mapIndexed { i, _ ->
        rememberChartAnimationState(style.animation, dataKey, seriesIndex = i)
    }

    val (xRange, yRange) = remember(data) { AxisRangeCalculator.fromData(data) }
    val (_, yTicks) = remember(yRange) {
        AxisRangeCalculator.nice(yRange.min, yRange.max, style.axisStyle.yTickCount)
    }

    val tooltipState = rememberTooltipState()

    val legendStyle = style.legendStyle
    val showLegend = legendStyle.visible && data.series.size > 1
    val effectiveLegendStyle = if (
        legendStyle.position in listOf(LegendPosition.LEFT, LegendPosition.RIGHT) &&
        legendStyle.orientation == LegendOrientation.HORIZONTAL
    ) legendStyle.copy(orientation = LegendOrientation.VERTICAL) else legendStyle

    val tapHandler = Modifier.pointerInput(data, style) {
        if (onDataPointClick == null && style.tooltipStyle == null) return@pointerInput
        detectTapGestures { tapOffset ->
            val mapper = ChartCoordinateMapper(
                canvasSize = Size(size.width.toFloat(), size.height.toFloat()),
                xRange = xRange,
                yRange = yRange,
                paddingPx = 48f,
            )
            val hit = HitTestHelper.nearestDataPoint(tapOffset, mapper, data.series)
            if (hit != null) {
                onDataPointClick?.invoke(hit.first, hit.second)
                style.tooltipStyle?.let {
                    tooltipState.show(tapOffset, hit.second, data.series[hit.first].label)
                }
            }
        }
    }

    val canvas: @Composable (Modifier) -> Unit = { canvasMod ->
        Canvas(modifier = canvasMod) {
            val paddingPx = 48f
            val mapper = ChartCoordinateMapper(size, xRange, yRange, paddingPx)
            drawGrid(mapper, yTicks, style.axisStyle, theme)
            drawAxes(mapper, yTicks, data.series, style.axisStyle, theme, textMeasurer)
            data.series.forEachIndexed { i, series ->
                drawLineSeries(series, seriesColors[i], mapper, style, animStates[i].fraction)
            }
        }
    }

    Box(modifier = modifier) {
        when (legendStyle.position) {
            LegendPosition.BOTTOM -> Column(Modifier.matchParentSize()) {
                canvas(Modifier.weight(1f).fillMaxWidth().then(tapHandler))
                if (showLegend) {
                    Spacer(Modifier.height(4.dp))
                    ChartLegend(data.series, seriesColors, effectiveLegendStyle, Modifier.fillMaxWidth(), theme)
                }
            }
            LegendPosition.TOP -> Column(Modifier.matchParentSize()) {
                if (showLegend) {
                    ChartLegend(data.series, seriesColors, effectiveLegendStyle, Modifier.fillMaxWidth(), theme)
                    Spacer(Modifier.height(4.dp))
                }
                canvas(Modifier.weight(1f).fillMaxWidth().then(tapHandler))
            }
            LegendPosition.LEFT -> Row(Modifier.matchParentSize()) {
                if (showLegend) {
                    ChartLegend(data.series, seriesColors, effectiveLegendStyle, Modifier.wrapContentWidth(), theme)
                    Spacer(Modifier.width(8.dp))
                }
                canvas(Modifier.weight(1f).fillMaxWidth().then(tapHandler))
            }
            LegendPosition.RIGHT -> Row(Modifier.matchParentSize()) {
                canvas(Modifier.weight(1f).fillMaxWidth().then(tapHandler))
                if (showLegend) {
                    Spacer(Modifier.width(8.dp))
                    ChartLegend(data.series, seriesColors, effectiveLegendStyle, Modifier.wrapContentWidth(), theme)
                }
            }
        }
        style.tooltipStyle?.let { ChartTooltip(tooltipState, it) }
    }
}
