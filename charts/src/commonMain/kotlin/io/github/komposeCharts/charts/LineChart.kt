package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.axis.AxisRangeCalculator
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.internal.HitTestHelper
import io.github.komposeCharts.internal.rememberChartAnimationState
import io.github.komposeCharts.internal.resolveSeriesColor
import io.github.komposeCharts.renderer.ChartLegend
import io.github.komposeCharts.renderer.ChartTooltip
import io.github.komposeCharts.renderer.drawAxes
import io.github.komposeCharts.renderer.drawCrosshair
import io.github.komposeCharts.renderer.drawGrid
import io.github.komposeCharts.renderer.drawLines
import io.github.komposeCharts.renderer.drawReferenceLines
import io.github.komposeCharts.style.LegendPosition
import io.github.komposeCharts.style.LineChartStyle
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
    val textMeasurer = rememberTextMeasurer()
    val dataKey = data.hashCode()

    // Animation state per series.
    val animStates = data.series.mapIndexed { idx, _ ->
        rememberChartAnimationState(style.animation, dataKey, idx)
    }
    val fractions = animStates.map { it.fraction }

    // Interactive legend: hidden series.
    var hiddenSeries by remember { mutableStateOf(emptySet<Int>()) }

    // Selection state: (seriesIndex, point.x as int).
    var selectedPoint by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // Pointer position for crosshair.
    var pointerOffset by remember { mutableStateOf<Offset?>(null) }

    // Tooltip state.
    var tooltipText by remember { mutableStateOf<String?>(null) }

    // Filter data by hidden series.
    val visibleData = remember(data, hiddenSeries) {
        if (hiddenSeries.isEmpty()) data
        else ChartData(data.series.filterIndexed { idx, _ -> idx !in hiddenSeries })
    }

    // Axis ranges: x is continuous (from data), y is "nice"-rounded.
    val (xRange, rawYRange) = remember(visibleData) {
        AxisRangeCalculator.fromData(visibleData)
    }
    val (niceYRange, yTicks) = remember(rawYRange, style.axisStyle.yTickCount) {
        AxisRangeCalculator.nice(rawYRange.min, rawYRange.max, style.axisStyle.yTickCount)
    }

    // X ticks and labels come from the first series' points.
    val firstSeries = visibleData.series.firstOrNull()
    val xTicks = remember(visibleData) {
        firstSeries?.points?.filter { !it.isMissing }?.map { it.x } ?: emptyList()
    }
    val xLabels = remember(visibleData) {
        firstSeries?.points?.filter { !it.isMissing }?.map { it.label }
    }

    // Resolve series colors for legend.
    val seriesColors = remember(data, theme, style.lineColors) {
        data.series.mapIndexed { idx, series ->
            resolveSeriesColor(idx, series, theme, style.lineColors)
        }
    }

    // Point rects for hit testing (updated after each draw).
    var pointRects by remember { mutableStateOf<List<Triple<Int, DataPoint, Rect>>>(emptyList()) }

    val legendContent: @Composable () -> Unit = {
        if (data.series.size > 1) {
            ChartLegend(
                labels = data.series.map { it.label },
                colors = seriesColors,
                style = style.legendStyle,
                theme = theme,
                hiddenSeries = hiddenSeries,
                onToggle = if (style.legendStyle.interactive) {
                    { idx ->
                        hiddenSeries = if (idx in hiddenSeries) hiddenSeries - idx else hiddenSeries + idx
                    }
                } else null,
            )
        }
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
                                val hit = HitTestHelper.hitBar(offset, pointRects)
                                if (hit != null) {
                                    selectedPoint = hit.first to hit.second.x.toInt()
                                    onPointClick?.invoke(hit.first, hit.second)

                                    style.tooltipStyle?.let {
                                        val formatter = { v: Float ->
                                            if (v == v.toLong().toFloat()) v.toLong().toString()
                                            else "${(v * 10).toInt() / 10.0}"
                                        }
                                        tooltipText = "${hit.second.label ?: "Point ${hit.second.x.toInt()}"}: ${formatter(hit.second.y)}"
                                    }
                                } else {
                                    selectedPoint = null
                                    tooltipText = null
                                }
                            }
                        }
                        .pointerInput(style.crosshairStyle) {
                            if (style.crosshairStyle != null) {
                                detectDragGestures(
                                    onDragStart = { offset -> pointerOffset = offset },
                                    onDrag = { change, _ ->
                                        pointerOffset = change.position
                                        change.consume()
                                    },
                                    onDragEnd = { pointerOffset = null },
                                    onDragCancel = { pointerOffset = null },
                                )
                            }
                        }
                        .pointerInput(style.crosshairStyle) {
                            if (style.crosshairStyle != null) {
                                awaitPointerEventScope {
                                    while (true) {
                                        val event = awaitPointerEvent()
                                        when (event.type) {
                                            PointerEventType.Move ->
                                                pointerOffset = event.changes.firstOrNull()?.position
                                            PointerEventType.Exit ->
                                                pointerOffset = null
                                        }
                                    }
                                }
                            }
                        },
                ) {
                    val paddingPx = 40f
                    val mapper = ChartCoordinateMapper(size, xRange, niceYRange, paddingPx)

                    // 1. Grid
                    drawGrid(yTicks, mapper, style.axisStyle, theme)

                    // 2. Axes
                    drawAxes(xTicks, yTicks, mapper, style.axisStyle, theme, textMeasurer, xLabels)

                    // 3. Lines
                    pointRects = drawLines(
                        data = visibleData,
                        mapper = mapper,
                        style = style,
                        fractions = fractions,
                        theme = theme,
                        selectedPoint = selectedPoint,
                    )

                    // 4. Reference lines
                    if (style.referenceLines.isNotEmpty()) {
                        drawReferenceLines(style.referenceLines, mapper, theme, textMeasurer)
                    }

                    // 5. Crosshair
                    val crosshair = style.crosshairStyle
                    val ptr = pointerOffset
                    if (crosshair != null && ptr != null) {
                        drawCrosshair(ptr, mapper, crosshair)
                    }
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
