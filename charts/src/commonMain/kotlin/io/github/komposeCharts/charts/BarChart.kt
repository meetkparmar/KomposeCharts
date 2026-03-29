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
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.animation.ChartAnimationState
import io.github.komposeCharts.animation.rememberChartAnimationState
import io.github.komposeCharts.core.axis.AxisRange
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
import io.github.komposeCharts.renderer.drawBars
import io.github.komposeCharts.renderer.drawGrid
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.LegendOrientation
import io.github.komposeCharts.style.LegendPosition
import io.github.komposeCharts.theme.ChartTheme
import io.github.komposeCharts.theme.LocalChartTheme

/**
 * Renders a bar chart from [data].
 *
 * Supports vertical/horizontal orientation, grouped/stacked grouping, optional value labels,
 * and an animated grow-in effect where bars grow up from the baseline.
 *
 * @param data The chart data to render.
 * @param modifier Layout modifier for sizing the chart.
 * @param style Visual and animation configuration.
 * @param onBarClick Optional callback fired when a bar is tapped.
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
    val textMeasurer = rememberTextMeasurer()
    val dataKey = remember(data) { data.hashCode() }

    val seriesColors = data.series.mapIndexed { i, s ->
        resolveSeriesColor(i, s, theme, style.barColors)
    }

    val animStates = data.series.mapIndexed { i, _ ->
        rememberChartAnimationState(style.animation, dataKey, seriesIndex = i)
    }

    val (xRange, yRange) = remember(data) { AxisRangeCalculator.fromData(data) }
    val (_, yTicks) = remember(yRange) {
        AxisRangeCalculator.nice(yRange.min, yRange.max, style.axisStyle.yTickCount)
    }

    val barRects = remember { mutableStateListOf<Triple<Int, DataPoint, Rect>>() }
    val tooltipState = rememberTooltipState()

    val legendStyle = style.legendStyle
    val showLegend = legendStyle.visible && data.series.size > 1
    val effectiveLegendStyle = if (
        legendStyle.position in listOf(LegendPosition.LEFT, LegendPosition.RIGHT) &&
        legendStyle.orientation == LegendOrientation.HORIZONTAL
    ) legendStyle.copy(orientation = LegendOrientation.VERTICAL) else legendStyle

    val tapHandler = Modifier.pointerInput(data) {
        if (onBarClick == null && style.tooltipStyle == null) return@pointerInput
        detectTapGestures { tapOffset ->
            val hit = HitTestHelper.hitBar(tapOffset, barRects)
            if (hit != null) {
                onBarClick?.invoke(hit.first, hit.second)
                style.tooltipStyle?.let {
                    tooltipState.show(tapOffset, hit.second, data.series[hit.first].label)
                }
            }
        }
    }

    Box(modifier = modifier) {
        when (legendStyle.position) {
            LegendPosition.BOTTOM -> Column(Modifier.matchParentSize()) {
                BarCanvas(Modifier.weight(1f).fillMaxWidth().then(tapHandler),
                    data, seriesColors, xRange, yRange, yTicks, animStates, style, theme, textMeasurer, barRects)
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
                BarCanvas(Modifier.weight(1f).fillMaxWidth().then(tapHandler),
                    data, seriesColors, xRange, yRange, yTicks, animStates, style, theme, textMeasurer, barRects)
            }
            LegendPosition.LEFT -> Row(Modifier.matchParentSize()) {
                if (showLegend) {
                    ChartLegend(data.series, seriesColors, effectiveLegendStyle, Modifier.wrapContentWidth(), theme)
                    Spacer(Modifier.width(8.dp))
                }
                BarCanvas(Modifier.weight(1f).fillMaxWidth().then(tapHandler),
                    data, seriesColors, xRange, yRange, yTicks, animStates, style, theme, textMeasurer, barRects)
            }
            LegendPosition.RIGHT -> Row(Modifier.matchParentSize()) {
                BarCanvas(Modifier.weight(1f).fillMaxWidth().then(tapHandler),
                    data, seriesColors, xRange, yRange, yTicks, animStates, style, theme, textMeasurer, barRects)
                if (showLegend) {
                    Spacer(Modifier.width(8.dp))
                    ChartLegend(data.series, seriesColors, effectiveLegendStyle, Modifier.wrapContentWidth(), theme)
                }
            }
        }
        style.tooltipStyle?.let { ChartTooltip(tooltipState, it) }
    }
}

@Composable
private fun BarCanvas(
    modifier: Modifier,
    data: ChartData,
    seriesColors: List<Color>,
    xRange: AxisRange,
    yRange: AxisRange,
    yTicks: List<Float>,
    animStates: List<ChartAnimationState>,
    style: BarChartStyle,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    barRects: SnapshotStateList<Triple<Int, DataPoint, Rect>>,
) {
    Canvas(modifier = modifier) {
        val paddingPx = 48f
        val mapper = ChartCoordinateMapper(size, xRange, yRange, paddingPx)
        val fractions = animStates.map { it.fraction }
        drawGrid(mapper, yTicks, style.axisStyle, theme)
        drawAxes(mapper, yTicks, data.series, style.axisStyle, theme, textMeasurer)
        val newRects = drawBars(data, seriesColors, mapper, style, fractions, theme, textMeasurer)
        barRects.clear()
        barRects.addAll(newRects)
    }
}
