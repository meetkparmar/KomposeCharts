package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.internal.HitTestHelper
import io.github.komposeCharts.internal.rememberChartAnimationState
import io.github.komposeCharts.renderer.ChartLegend
import io.github.komposeCharts.renderer.ChartTooltip
import io.github.komposeCharts.renderer.SliceArc
import io.github.komposeCharts.renderer.drawPie
import io.github.komposeCharts.renderer.pieSliceColor
import io.github.komposeCharts.style.LegendPosition
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
    val textMeasurer = rememberTextMeasurer()
    val dataKey = data.hashCode()

    val animState = rememberChartAnimationState(style.animation, dataKey, 0)
    val fraction = animState.fraction

    var hiddenSlices by remember { mutableStateOf(emptySet<Int>()) }
    var selectedSlice by remember { mutableStateOf<Int?>(null) }
    var tooltipText by remember { mutableStateOf<String?>(null) }

    var sliceArcs by remember { mutableStateOf<List<SliceArc>>(emptyList()) }

    // Legend entries map to slices (the first series' points).
    val sliceColors = remember(series, theme, style.sliceColors) {
        series.points.indices.map { pieSliceColor(it, style, theme) }
    }
    val sliceLabels = remember(series) {
        series.points.mapIndexed { i, p -> p.label ?: "Slice ${i + 1}" }
    }

    val legendContent: @Composable () -> Unit = {
        ChartLegend(
            labels = sliceLabels,
            colors = sliceColors,
            style = style.legendStyle,
            theme = theme,
            hiddenSeries = hiddenSlices,
            onToggle = if (style.legendStyle.interactive) {
                { idx ->
                    hiddenSlices = if (idx in hiddenSlices) hiddenSlices - idx else hiddenSlices + idx
                }
            } else null,
        )
    }

    val isLegendLeft = style.legendStyle.position == LegendPosition.LEFT
    val isLegendRight = style.legendStyle.position == LegendPosition.RIGHT
    val isLegendTop = style.legendStyle.position == LegendPosition.TOP
    val isLegendBottom = style.legendStyle.position == LegendPosition.BOTTOM

    Column(modifier = modifier) {
        if (isLegendTop) legendContent()

        Row(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (isLegendLeft) legendContent()

            Box(modifier = Modifier.weight(1f)) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(theme.contentPadding)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val hit = HitTestHelper.hitSlice(offset, sliceArcs)
                                if (hit != null) {
                                    selectedSlice = hit.first
                                    onSliceClick?.invoke(hit.first, hit.second)
                                    style.tooltipStyle?.let {
                                        val total = series.points
                                            .filterIndexed { i, _ -> i !in hiddenSlices }
                                            .sumOf { p -> if (p.isMissing || p.y < 0f) 0.0 else p.y.toDouble() }
                                            .toFloat()
                                        val pct = if (total > 0f) (hit.second.y / total * 100f).roundToInt() else 0
                                        tooltipText = "${hit.second.label ?: "Slice ${hit.first + 1}"}: $pct%"
                                    }
                                } else {
                                    selectedSlice = null
                                    tooltipText = null
                                }
                            }
                        },
                ) {
                    sliceArcs = drawPie(
                        series = series,
                        style = style,
                        fraction = fraction,
                        theme = theme,
                        textMeasurer = textMeasurer,
                        hiddenSlices = hiddenSlices,
                        selectedSlice = selectedSlice,
                    )
                }

                val tooltip = tooltipText
                val tooltipSty = style.tooltipStyle
                if (tooltip != null && tooltipSty != null) {
                    ChartTooltip(
                        text = tooltip,
                        style = tooltipSty,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 8.dp),
                    )
                }
            }

            if (isLegendRight) legendContent()
        }

        if (isLegendBottom) legendContent()
    }
}
