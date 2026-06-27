package io.github.komposeCharts.charts

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
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
import io.github.komposeCharts.renderer.drawBars
import io.github.komposeCharts.renderer.drawCrosshair
import io.github.komposeCharts.renderer.drawGrid
import io.github.komposeCharts.renderer.drawReferenceLines
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.BarGrouping
import io.github.komposeCharts.style.BarOrientation
import io.github.komposeCharts.style.LegendPosition
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
    val textMeasurer = rememberTextMeasurer()
    val dataKey = data.hashCode()

    // Animation state per series
    val animStates = data.series.mapIndexed { idx, _ ->
        rememberChartAnimationState(style.animation, dataKey, idx)
    }
    val fractions = animStates.map { it.fraction }

    // Interactive legend: hidden series
    var hiddenSeries by remember { mutableStateOf(emptySet<Int>()) }

    // Selection state
    var selectedBar by remember { mutableStateOf<Pair<Int, Int>?>(null) }

    // Pointer position for crosshair
    var pointerOffset by remember { mutableStateOf<Offset?>(null) }

    // Tooltip state
    var tooltipText by remember { mutableStateOf<String?>(null) }

    // Filter data by hidden series
    val visibleData = remember(data, hiddenSeries) {
        if (hiddenSeries.isEmpty()) data
        else ChartData(data.series.filterIndexed { idx, _ -> idx !in hiddenSeries })
    }

    // Compute axis ranges
    val (rawXRange, rawYRange) = remember(visibleData, style.grouping) {
        if (style.grouping == BarGrouping.PERCENT_STACKED) {
            AxisRangeCalculator.fromData(visibleData) // y-range overridden below
        } else {
            AxisRangeCalculator.fromData(visibleData)
        }
    }

    val yRange = remember(rawYRange, style.grouping) {
        if (style.grouping == BarGrouping.PERCENT_STACKED) {
            io.github.komposeCharts.core.axis.AxisRange(0f, 100f)
        } else rawYRange
    }

    val (niceYRange, yTicks) = remember(yRange, style.axisStyle.yTickCount) {
        AxisRangeCalculator.nice(yRange.min, yRange.max, style.axisStyle.yTickCount)
    }

    // X ticks = category indices
    val categoryCount = visibleData.series.maxOfOrNull { it.points.size } ?: 0
    val xTicks = (0 until categoryCount).map { it.toFloat() }
    val xRange = remember(categoryCount) {
        if (categoryCount <= 1) io.github.komposeCharts.core.axis.AxisRange(-0.5f, 0.5f)
        else io.github.komposeCharts.core.axis.AxisRange(-0.5f, categoryCount - 0.5f)
    }

    // Collect x-axis labels from the first series' data points
    val xLabels = remember(visibleData) {
        visibleData.series.firstOrNull()?.points?.map { it.label }
    }

    // Resolve series colors for legend
    val seriesColors = remember(data, theme, style.barColors) {
        data.series.mapIndexed { idx, series ->
            resolveSeriesColor(idx, series, theme, style.barColors)
        }
    }

    // Bar rects for hit testing (updated after each draw)
    var barRects by remember { mutableStateOf<List<Triple<Int, DataPoint, Rect>>>(emptyList()) }

    // Legend composable
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

    // Y-axis label formatter for percent mode
    val effectiveAxisStyle = remember(style.axisStyle, style.grouping) {
        if (style.grouping == BarGrouping.PERCENT_STACKED && style.axisStyle.yLabelFormatter == null) {
            style.axisStyle.copy(yLabelFormatter = { "${it.toInt()}%" })
        } else style.axisStyle
    }

    // Layout
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

            Box(
                modifier = Modifier.weight(1f),
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .padding(theme.contentPadding)
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val hit = HitTestHelper.hitBar(offset, barRects)
                                if (hit != null) {
                                    selectedBar = hit.first to hit.second.x.toInt()
                                    onBarClick?.invoke(hit.first, hit.second)

                                    // Tooltip
                                    style.tooltipStyle?.let {
                                        val formatter = style.valueLabelFormatter ?: { v ->
                                            if (v == v.toLong().toFloat()) v.toLong().toString()
                                            else "${(v * 10).toInt() / 10.0}"
                                        }
                                        tooltipText = "${hit.second.label ?: "Item ${hit.second.x.toInt()}"}: ${formatter(hit.second.y)}"
                                    }
                                } else {
                                    selectedBar = null
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
                                            PointerEventType.Move -> {
                                                pointerOffset = event.changes.firstOrNull()?.position
                                            }
                                            PointerEventType.Exit -> {
                                                pointerOffset = null
                                            }
                                        }
                                    }
                                }
                            }
                        },
                ) {
                    val paddingPx = 40f
                    val mapper = ChartCoordinateMapper(size, xRange, niceYRange, paddingPx)

                    // 1. Grid
                    drawGrid(yTicks, mapper, effectiveAxisStyle, theme)

                    // 2. Axes
                    drawAxes(xTicks, yTicks, mapper, effectiveAxisStyle, theme, textMeasurer, xLabels)

                    // 3. Bars
                    barRects = drawBars(
                        data = visibleData,
                        mapper = mapper,
                        style = style,
                        fractions = fractions,
                        theme = theme,
                        textMeasurer = textMeasurer,
                        selectedBar = selectedBar,
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

                // Tooltip overlay
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
