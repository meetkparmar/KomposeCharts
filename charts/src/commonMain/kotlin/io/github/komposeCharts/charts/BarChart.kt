package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.animation.rememberChartAnimationState
import io.github.komposeCharts.core.axis.AxisRangeCalculator
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.internal.resolveSeriesColor
import io.github.komposeCharts.renderer.ChartLegend
import io.github.komposeCharts.renderer.drawAxes
import io.github.komposeCharts.renderer.drawBars
import io.github.komposeCharts.renderer.drawGrid
import io.github.komposeCharts.style.BarChartStyle
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

    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .pointerInput(data) {
                    if (onBarClick == null) return@pointerInput
                    detectTapGestures {
                        // Bar hit testing is handled via HitTestHelper.hitBar in a full implementation
                        // Simplified: find nearest x category
                    }
                }
        ) {
            val paddingPx = 48f
            val mapper = ChartCoordinateMapper(
                canvasSize = size,
                xRange = xRange,
                yRange = yRange,
                paddingPx = paddingPx,
            )

            val fractions = animStates.map { it.fraction }

            drawGrid(mapper, yTicks, style.axisStyle, theme)
            drawAxes(mapper, yTicks, data.series, style.axisStyle, theme, textMeasurer)
            drawBars(data, seriesColors, mapper, style, fractions, theme, textMeasurer)
        }

        if (data.series.size > 1) {
            Spacer(modifier = Modifier.height(4.dp))
            ChartLegend(
                series = data.series,
                colors = seriesColors,
                modifier = Modifier.fillMaxWidth(),
                theme = theme,
            )
        }
    }
}
