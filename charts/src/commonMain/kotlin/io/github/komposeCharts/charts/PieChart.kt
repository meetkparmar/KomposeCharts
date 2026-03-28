package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import io.github.komposeCharts.internal.resolveSeriesColor
import io.github.komposeCharts.renderer.ChartLegend
import io.github.komposeCharts.renderer.drawPie
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

    // Build a fake DataSeries list to reuse ChartLegend
    val legendSeries = series.points.mapIndexed { i, point ->
        io.github.komposeCharts.core.data.DataSeries(
            label = point.label ?: "Slice ${i + 1}",
            points = listOf(point),
            colorToken = i,
        )
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .pointerInput(data, style) {
                    if (onSliceClick == null) return@pointerInput
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
                        val hit = io.github.komposeCharts.interaction.HitTestHelper.hitPieSlice(
                            tapOffset, sliceAngles, Offset(cx, cy), radius, innerRadius
                        )
                        if (hit != null) onSliceClick(0, hit)
                    }
                }
        ) {
            drawPie(series, sliceColors, style, animState.fraction, theme, textMeasurer)
        }

        if (series.points.size > 1) {
            Spacer(modifier = Modifier.height(8.dp))
            ChartLegend(
                series = legendSeries,
                colors = sliceColors,
                modifier = Modifier.fillMaxWidth(),
                theme = theme,
            )
        }
    }
}
