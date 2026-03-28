package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
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
import io.github.komposeCharts.renderer.drawGrid
import io.github.komposeCharts.renderer.drawLineSeries
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

    // Per-series animation states (staggered)
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
                .pointerInput(data, style) {
                    if (onDataPointClick == null) return@pointerInput
                    detectTapGestures { tapOffset ->
                        val mapper = ChartCoordinateMapper(
                            canvasSize = androidx.compose.ui.geometry.Size(size.width.toFloat(), size.height.toFloat()),
                            xRange = xRange,
                            yRange = yRange,
                            paddingPx = 48f,
                        )
                        val hit = io.github.komposeCharts.interaction.HitTestHelper.nearestDataPoint(
                            tapOffset, mapper, data.series
                        )
                        if (hit != null) onDataPointClick(hit.first, hit.second)
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

            drawGrid(mapper, yTicks, style.axisStyle, theme)
            drawAxes(mapper, yTicks, data.series, style.axisStyle, theme, textMeasurer)

            data.series.forEachIndexed { i, series ->
                val fraction = animStates[i].fraction
                drawLineSeries(series, seriesColors[i], mapper, style, fraction)
            }
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
