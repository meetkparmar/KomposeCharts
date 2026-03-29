package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.animation.rememberChartAnimationState
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.interaction.HitTestHelper
import io.github.komposeCharts.interaction.rememberTooltipState
import io.github.komposeCharts.internal.resolveSeriesColor
import io.github.komposeCharts.renderer.ChartLegend
import io.github.komposeCharts.renderer.ChartTooltip
import io.github.komposeCharts.renderer.drawPie
import io.github.komposeCharts.style.LegendOrientation
import io.github.komposeCharts.style.LegendPosition
import io.github.komposeCharts.style.PieChartStyle
import io.github.komposeCharts.theme.LocalChartTheme
import kotlin.math.min

/**
 * Renders a pie or donut chart from the first series in [data].
 *
 * Each [DataPoint] in the series represents one slice. The [DataPoint.y] value determines
 * the slice size (as a fraction of the total). [DataPoint.label] is used for slice labels.
 *
 * Set [PieChartStyle.innerRadiusFraction] > 0 for a donut chart.
 *
 * @param data The chart data to render (only the first series is used).
 * @param modifier Layout modifier for sizing the chart.
 * @param style Visual and animation configuration.
 * @param onSliceClick Optional callback fired when a slice is tapped.
 */
@Composable
fun PieChart(
    data: ChartData,
    modifier: Modifier = Modifier,
    style: PieChartStyle = PieChartStyle(),
    onSliceClick: ((seriesIndex: Int, point: DataPoint) -> Unit)? = null,
) {
    if (data.isEmpty) return

    val theme = LocalChartTheme.current
    val textMeasurer = rememberTextMeasurer()
    val dataKey = remember(data) { data.hashCode() }

    val series = data.series[0]
    val sliceColors = series.points.mapIndexed { i, _ ->
        resolveSeriesColor(i, series, theme, style.sliceColors)
    }

    val animState = rememberChartAnimationState(style.animation, dataKey, seriesIndex = 0)
    val tooltipState = rememberTooltipState()

    // Build a fake DataSeries list to reuse ChartLegend
    val legendSeries = series.points.mapIndexed { i, point ->
        DataSeries(
            label = point.label ?: "Slice ${i + 1}",
            points = listOf(point),
            colorToken = i,
        )
    }

    val legendStyle = style.legendStyle
    val showLegend = legendStyle.visible
    val effectiveLegendStyle = if (
        legendStyle.position in listOf(LegendPosition.LEFT, LegendPosition.RIGHT) &&
        legendStyle.orientation == LegendOrientation.HORIZONTAL
    ) legendStyle.copy(orientation = LegendOrientation.VERTICAL) else legendStyle

    val canvasModifier = Modifier
        .fillMaxWidth()
        .aspectRatio(1f)
        .pointerInput(data, style) {
            if (onSliceClick == null && style.tooltipStyle == null) return@pointerInput
            detectTapGestures { tapOffset ->
                val cx = size.width / 2f
                val cy = size.height / 2f
                val radius = min(size.width, size.height) / 2f * 0.85f
                val innerRadius = radius * style.innerRadiusFraction
                val total = series.points.sumOf { it.y.toDouble() }.toFloat()
                var currentAngle = style.startAngleDeg
                val sliceAngles = series.points.map { point ->
                    val sweep = (point.y / total) * 360f
                    val triple = Triple(currentAngle, sweep, point)
                    currentAngle += sweep
                    triple
                }
                val hit = HitTestHelper.hitPieSlice(tapOffset, sliceAngles, Offset(cx, cy), radius, innerRadius)
                if (hit != null) {
                    onSliceClick?.invoke(0, hit)
                    style.tooltipStyle?.let { tooltipState.show(tapOffset, hit, series.label) }
                }
            }
        }

    val canvas: @Composable (Modifier) -> Unit = { mod ->
        Canvas(modifier = mod) {
            drawPie(series, sliceColors, style, animState.fraction, theme, textMeasurer)
        }
    }

    Box(modifier = modifier) {
        when (legendStyle.position) {
            LegendPosition.BOTTOM -> Column(
                modifier = Modifier.matchParentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                canvas(canvasModifier)
                if (showLegend) {
                    Spacer(Modifier.height(8.dp))
                    ChartLegend(legendSeries, sliceColors, effectiveLegendStyle, Modifier.fillMaxWidth(), theme)
                }
            }
            LegendPosition.TOP -> Column(
                modifier = Modifier.matchParentSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                if (showLegend) {
                    ChartLegend(legendSeries, sliceColors, effectiveLegendStyle, Modifier.fillMaxWidth(), theme)
                    Spacer(Modifier.height(8.dp))
                }
                canvas(canvasModifier)
            }
            LegendPosition.LEFT -> Row(Modifier.matchParentSize()) {
                if (showLegend) {
                    ChartLegend(legendSeries, sliceColors, effectiveLegendStyle, Modifier.wrapContentWidth(), theme)
                    Spacer(Modifier.width(8.dp))
                }
                canvas(canvasModifier.weight(1f))
            }
            LegendPosition.RIGHT -> Row(Modifier.matchParentSize()) {
                canvas(canvasModifier.weight(1f))
                if (showLegend) {
                    Spacer(Modifier.width(8.dp))
                    ChartLegend(legendSeries, sliceColors, effectiveLegendStyle, Modifier.wrapContentWidth(), theme)
                }
            }
        }
        style.tooltipStyle?.let { ChartTooltip(tooltipState, it) }
    }
}
