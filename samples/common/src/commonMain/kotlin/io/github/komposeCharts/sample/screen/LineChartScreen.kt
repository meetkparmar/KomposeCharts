package io.github.komposeCharts.sample.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.komposeCharts.charts.LineChart
import io.github.komposeCharts.core.animation.ChartAnimation
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.sample.component.KCChip
import io.github.komposeCharts.sample.component.KCCodeBlock
import io.github.komposeCharts.sample.component.KCDataTable
import io.github.komposeCharts.sample.component.KCSectionLabel
import io.github.komposeCharts.sample.component.KCTableRow
import io.github.komposeCharts.sample.component.KCTopBar
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography
import io.github.komposeCharts.style.AxisStyle
import io.github.komposeCharts.style.CrosshairStyle
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.LineChartStyle
import io.github.komposeCharts.style.ReferenceLine
import io.github.komposeCharts.style.SelectionStyle
import io.github.komposeCharts.style.TooltipStyle
import kotlin.math.roundToInt
import kotlin.random.Random

private val LABELS = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val DEFAULT_VALUES = listOf(28f, 45f, 39f, 62f, 50f, 71f, 58f)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LineChartScreen(
    statusBarDp: Int = 0,
    onBack: () -> Unit = {},
) {
    // ── Option states ──
    var colorful by remember { mutableStateOf(false) }
    var gridLines by remember { mutableStateOf(true) }
    var animate by remember { mutableStateOf(true) }
    var markers by remember { mutableStateOf(true) }
    var smooth by remember { mutableStateOf(false) }
    var areaFill by remember { mutableStateOf(false) }
    var multiSeries by remember { mutableStateOf(false) }
    var refLine by remember { mutableStateOf(false) }
    var tooltip by remember { mutableStateOf(false) }
    var crosshair by remember { mutableStateOf(false) }

    var dataVersion by remember { mutableStateOf(0) }

    // ── Data ──
    val currentValues = remember(dataVersion) {
        if (dataVersion == 0) DEFAULT_VALUES
        else {
            val rng = Random(dataVersion)
            LABELS.map { (rng.nextFloat() * 70 + 10).roundToInt().toFloat() }
        }
    }
    val secondaryValues = remember(dataVersion) {
        val rng = Random(dataVersion + 1000)
        LABELS.map { (rng.nextFloat() * 55 + 5).roundToInt().toFloat() }
    }

    val data = remember(currentValues, secondaryValues, multiSeries) {
        val primary = DataSeries(
            label = "Sessions",
            points = currentValues.mapIndexed { i, v -> DataPoint(i.toFloat(), v, LABELS[i]) },
        )
        if (multiSeries) {
            val secondary = DataSeries(
                label = "Signups",
                points = secondaryValues.mapIndexed { i, v -> DataPoint(i.toFloat(), v, LABELS[i]) },
            )
            ChartData(listOf(primary, secondary))
        } else {
            ChartData(listOf(primary))
        }
    }

    // ── Style ──
    val accent = AppColors.AccentDefault
    val lineColors = when {
        colorful -> AppColors.ChartCategorical
        multiSeries -> null // theme palette
        else -> listOf(accent)
    }

    val referenceLines = if (refLine) {
        val avg = currentValues.average().toFloat()
        listOf(ReferenceLine(value = avg, label = "Avg ${avg.roundToInt()}"))
    } else emptyList()

    val style = LineChartStyle(
        lineColors = lineColors,
        smooth = smooth,
        showMarkers = markers,
        areaFill = areaFill,
        axisStyle = AxisStyle(showGrid = gridLines),
        selectionStyle = SelectionStyle(),
        crosshairStyle = if (crosshair) CrosshairStyle() else null,
        referenceLines = referenceLines,
        legendStyle = LegendStyle(interactive = multiSeries),
        tooltipStyle = if (tooltip) TooltipStyle() else null,
        animation = if (animate) ChartAnimation.Default else ChartAnimation.None,
    )

    // ── Dynamic code snippet ──
    val codeSnippet = remember(currentValues, colorful, gridLines, markers, smooth, areaFill, multiSeries, refLine, tooltip, crosshair) {
        buildString {
            appendLine("LineChart(")
            appendLine("    data = ChartData(")
            if (multiSeries) {
                appendLine("        listOf(")
                appendLine("            DataSeries(\"Sessions\", listOf(")
                currentValues.forEachIndexed { i, v ->
                    appendLine("                DataPoint(${i}f, ${v.roundToInt()}f, \"${LABELS[i]}\"),")
                }
                appendLine("            )),")
                appendLine("            DataSeries(\"Signups\", listOf(/* … */)),")
                appendLine("        ),")
            } else {
                appendLine("        listOf(${currentValues.mapIndexed { _, v -> "${v.roundToInt()}f" }.joinToString()}),")
                appendLine("        labels = listOf(${LABELS.joinToString { "\"$it\"" }}),")
            }
            appendLine("    ),")
            appendLine("    modifier = Modifier")
            appendLine("        .fillMaxWidth()")
            appendLine("        .height(220.dp),")
            appendLine("    style = LineChartStyle(")
            if (smooth) appendLine("        smooth = true,")
            if (markers) appendLine("        showMarkers = true,")
            if (areaFill) appendLine("        areaFill = true,")
            if (gridLines) appendLine("        axisStyle = AxisStyle(showGrid = true),")
            if (tooltip) appendLine("        tooltipStyle = TooltipStyle(),")
            if (crosshair) appendLine("        crosshairStyle = CrosshairStyle(),")
            if (refLine) appendLine("        referenceLines = listOf(ReferenceLine(${currentValues.average().roundToInt()}f)),")
            appendLine("    ),")
            append(")")
        }
    }

    // ── Dynamic table rows ──
    val tableRows = remember(currentValues) {
        currentValues.mapIndexed { i, v -> KCTableRow(LABELS[i], "${v.roundToInt()}") }
    }

    // ── Layout ──
    Column(modifier = Modifier.fillMaxSize()) {
        KCTopBar(
            title = "Line Chart",
            trailing = "LineChart()",
            statusBarHeight = statusBarDp,
            onBack = onBack,
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(AppDimen.Spacing_18dp),
            verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_18dp),
        ) {
            ChartPreviewCard(accent = accent, onReplay = { dataVersion++ }) {
                LineChart(
                    data = data,
                    modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_220dp),
                    style = style,
                )
            }

            Column {
                KCSectionLabel("Options")
                FlowRow(
                    modifier = Modifier.padding(top = AppDimen.Spacing_8dp),
                    horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
                    verticalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp),
                ) {
                    KCChip(label = "Colorful", active = colorful, accent = accent, onToggle = { colorful = it })
                    KCChip(label = "Grid lines", active = gridLines, accent = accent, onToggle = { gridLines = it })
                    KCChip(label = "Animate", active = animate, accent = accent, onToggle = {
                        animate = it; if (it) dataVersion++
                    })
                    KCChip(label = "Markers", active = markers, accent = accent, onToggle = { markers = it })
                    KCChip(label = "Smooth", active = smooth, accent = accent, onToggle = { smooth = it })
                    KCChip(label = "Area fill", active = areaFill, accent = accent, onToggle = { areaFill = it })
                    KCChip(label = "Multi-series", active = multiSeries, accent = accent, onToggle = { multiSeries = it })
                    KCChip(label = "Ref line", active = refLine, accent = accent, onToggle = { refLine = it })
                    KCChip(label = "Tooltip", active = tooltip, accent = accent, onToggle = { tooltip = it })
                    KCChip(label = "Crosshair", active = crosshair, accent = accent, onToggle = { crosshair = it })
                }
            }

            Column {
                KCSectionLabel("Use Case")
                Text(
                    text = "Track how a value changes over a continuous axis like time. The slope between points reveals trends, momentum, and turning points.",
                    modifier = Modifier.padding(top = AppDimen.Spacing_8dp),
                    color = AppColors.Body,
                    fontSize = AppTypography.Body,
                    lineHeight = AppTypography.descriptionLineHeight,
                )
                Text(
                    text = "Best for traffic over days, prices over time, or any trend you want to read at a glance.",
                    modifier = Modifier.padding(top = AppDimen.Spacing_8dp),
                    color = AppColors.TextMuted,
                    fontSize = AppTypography.Description,
                    lineHeight = AppTypography.descriptionLineHeight,
                )
            }

            KCCodeBlock(label = "Compose", accent = accent, code = codeSnippet)

            Column {
                KCSectionLabel("Sample Data")
                KCDataTable(
                    modifier = Modifier.padding(top = AppDimen.Spacing_8dp),
                    head0 = "Day",
                    head1 = "Sessions",
                    rows = tableRows,
                )
            }
        }
    }
}
