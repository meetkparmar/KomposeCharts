package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.charts.PieMath
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.internal.*
import io.github.komposeCharts.renderer.*
import io.github.komposeCharts.style.PieChartStyle
import io.github.komposeCharts.theme.LocalChartTheme
import kotlin.math.roundToInt

/**
 * A composable pie / donut chart.
 *
 * Renders the **first series** of [data]; each [DataPoint] becomes a slice
 * (its `y` is the slice size, its `label` the category name).
 *
 * Features:
 * - Pie and donut variants
 * - Clockwise sweep reveal animation
 * - Slice labels (percent / value / name) and an interactive legend
 * - Tap selection (explodes the selected slice) and tooltip
 *
 * @param data Chart data; only the first series is used.
 * @param modifier Modifier for the chart container.
 * @param style Visual and behavioral configuration.
 * @param onSliceClick Callback when a slice is tapped: (sliceIndex, dataPoint).
 */
@Composable
fun PieChart(
    data: ChartData,
    modifier: Modifier = Modifier,
    style: PieChartStyle = PieChartStyle(),
    onSliceClick: ((sliceIndex: Int, point: DataPoint) -> Unit)? = null,
) {
    if (data.isEmpty) return
    val series = data.series.firstOrNull() ?: return
    val theme = LocalChartTheme.current
    val measurer = rememberTextMeasurer()
    val state = rememberChartState(data)
    val fraction = rememberChartAnimationState(style.animation, data).fraction
    val colors = series.points.indices.map { pieSliceColor(it, style, theme) }
    val labels = series.points.mapIndexed { index, point -> point.label ?: "Slice ${index + 1}" }
    val input = Modifier.chartInput(state) { offset ->
        val hit = HitTestHelper.hitSlice(offset, state.slices)
        state.selection = hit?.let { 0 to it.first }
        state.tooltip = null
        if (hit != null) {
            onSliceClick?.invoke(hit.first, hit.second)
            if (style.tooltipStyle != null) {
                val values = series.points.mapIndexed { index, point ->
                    if (index in state.hidden || point.isMissing) 0f else point.y
                }
                val share = PieMath.computeSlices(values).getOrNull(hit.first)?.fraction ?: 0f
                state.tooltip = "${hit.second.label ?: "Slice ${hit.first + 1}"}: ${(share * 100).roundToInt()}%"
            }
        }
    }
    ChartFrame(modifier, state, labels, colors, style.legendStyle, style.tooltipStyle, theme) {
        Canvas(Modifier.fillMaxWidth().height(250.dp).padding(theme.contentPadding).then(input)) {
            state.slices = drawPie(series, style, fraction, theme, measurer, state.hidden, state.selection?.second)
        }
    }
}
