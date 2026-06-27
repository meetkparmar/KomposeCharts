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
import io.github.komposeCharts.charts.BarChart
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
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.BarGradient
import io.github.komposeCharts.style.BarGrouping
import io.github.komposeCharts.style.BarOrientation
import io.github.komposeCharts.style.CrosshairStyle
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.ReferenceLine
import io.github.komposeCharts.style.SelectionStyle
import io.github.komposeCharts.style.TooltipStyle
import io.github.komposeCharts.style.ValueLabelPosition
import kotlin.math.roundToInt
import kotlin.random.Random

private val LABELS = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
private val DEFAULT_VALUES = listOf(42f, 58f, 35f, 64f, 51f, 73f)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BarChartScreen(
    statusBarDp: Int = 0,
    onBack: () -> Unit = {},
) {
    // ── Option states ──
    var colorful by remember { mutableStateOf(false) }
    var gridLines by remember { mutableStateOf(true) }
    var animate by remember { mutableStateOf(true) }
    var gradient by remember { mutableStateOf(false) }
    var valueLabels by remember { mutableStateOf(false) }
    var refLine by remember { mutableStateOf(false) }
    var stacked by remember { mutableStateOf(false) }
    var horizontal by remember { mutableStateOf(false) }
    var multiSeries by remember { mutableStateOf(false) }
    var tooltip by remember { mutableStateOf(false) }
    var crosshair by remember { mutableStateOf(false) }
    var stroke by remember { mutableStateOf(false) }

    var dataVersion by remember { mutableStateOf(0) }

    // ── Data ──
    val currentValues = remember(dataVersion) {
        if (dataVersion == 0) DEFAULT_VALUES
        else {
            val rng = Random(dataVersion)
            LABELS.map { (rng.nextFloat() * 80 + 10).roundToInt().toFloat() }
        }
    }
    val secondaryValues = remember(dataVersion) {
        val rng = Random(dataVersion + 1000)
        LABELS.map { (rng.nextFloat() * 60 + 5).roundToInt().toFloat() }
    }

    val data = remember(currentValues, secondaryValues, multiSeries) {
        val primary = DataSeries(
            label = "Revenue",
            points = currentValues.mapIndexed { i, v -> DataPoint(i.toFloat(), v, LABELS[i]) },
        )
        if (multiSeries) {
            val secondary = DataSeries(
                label = "Expenses",
                points = secondaryValues.mapIndexed { i, v -> DataPoint(i.toFloat(), v, LABELS[i]) },
            )
            ChartData(listOf(primary, secondary))
        } else {
            ChartData(listOf(primary))
        }
    }

    // ── Style ──
    val accent = AppColors.AccentDefault
    val barColors = when {
        colorful -> AppColors.ChartCategorical
        multiSeries -> null // use theme palette
        else -> listOf(accent)
    }
    val barGradients = if (gradient) {
        (barColors ?: AppColors.ChartCategorical).map { c ->
            BarGradient(c, c.copy(alpha = 0.4f))
        }
    } else null

    val referenceLines = if (refLine) {
        val avg = currentValues.average().toFloat()
        listOf(ReferenceLine(value = avg, label = "Avg ${avg.roundToInt()}"))
    } else emptyList()

    val style = BarChartStyle(
        barColors = barColors,
        barGradients = barGradients,
        barStrokeWidth = if (stroke) AppDimen.BorderWidth else AppDimen.Spacing_0dp,
        orientation = if (horizontal) BarOrientation.HORIZONTAL else BarOrientation.VERTICAL,
        grouping = if (stacked && multiSeries) BarGrouping.STACKED else BarGrouping.GROUPED,
        axisStyle = AxisStyle(showGrid = gridLines),
        valueLabelPosition = if (valueLabels) ValueLabelPosition.TOP else ValueLabelPosition.NONE,
        valueLabelFormatter = { "${it.roundToInt()}" },
        selectionStyle = SelectionStyle(),
        crosshairStyle = if (crosshair) CrosshairStyle() else null,
        referenceLines = referenceLines,
        legendStyle = LegendStyle(interactive = multiSeries),
        tooltipStyle = if (tooltip) TooltipStyle() else null,
        animation = if (animate) ChartAnimation.Default else ChartAnimation.None,
    )

    // ── Dynamic code snippet ──
    val codeSnippet = remember(currentValues, colorful, gridLines, gradient, valueLabels, refLine, stacked, horizontal, multiSeries, tooltip, crosshair, stroke) {
        buildString {
            appendLine("BarChart(")
            appendLine("    data = ChartData(")
            if (multiSeries) {
                appendLine("        listOf(")
                appendLine("            DataSeries(\"Revenue\", listOf(")
                currentValues.forEachIndexed { i, v ->
                    appendLine("                DataPoint(${i}f, ${v.roundToInt()}f, \"${LABELS[i]}\"),")
                }
                appendLine("            )),")
                appendLine("            DataSeries(\"Expenses\", listOf(")
                secondaryValues.forEachIndexed { i, v ->
                    appendLine("                DataPoint(${i}f, ${v.roundToInt()}f, \"${LABELS[i]}\"),")
                }
                appendLine("            )),")
                appendLine("        ),")
            } else {
                appendLine("        listOf(${currentValues.mapIndexed { i, v -> "${v.roundToInt()}f" }.joinToString()}),")
                appendLine("        labels = listOf(${LABELS.joinToString { "\"$it\"" }}),")
            }
            appendLine("    ),")
            appendLine("    modifier = Modifier")
            appendLine("        .fillMaxWidth()")
            appendLine("        .height(220.dp),")
            appendLine("    style = BarChartStyle(")
            if (horizontal) appendLine("        orientation = HORIZONTAL,")
            if (stacked && multiSeries) appendLine("        grouping = STACKED,")
            if (gridLines) appendLine("        axisStyle = AxisStyle(showGrid = true),")
            if (valueLabels) appendLine("        valueLabelPosition = TOP,")
            if (gradient) appendLine("        barGradients = listOf(...),")
            if (stroke) appendLine("        barStrokeWidth = 1.dp,")
            if (tooltip) appendLine("        tooltipStyle = TooltipStyle(),")
            if (crosshair) appendLine("        crosshairStyle = CrosshairStyle(),")
            if (refLine) appendLine("        referenceLines = listOf(ReferenceLine(${currentValues.average().roundToInt()}f)),")
            appendLine("    ),")
            append(")")
        }
    }

    // ── Dynamic table rows ──
    val tableRows = remember(currentValues) {
        currentValues.mapIndexed { i, v ->
            KCTableRow(LABELS[i], "\$${v.roundToInt()}k")
        }
    }

    // ── Layout ──
    Column(
        modifier = Modifier.fillMaxSize(),
    ) {
        KCTopBar(
            title = "Bar Chart",
            trailing = "BarChart()",
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
            // ── PREVIEW ──
            ChartPreviewCard(
                accent = accent,
                onReplay = { dataVersion++ },
            ) {
                BarChart(
                    data = data,
                    modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_220dp),
                    style = style,
                )
            }

            // ── OPTIONS ──
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
                    KCChip(label = "Gradient", active = gradient, accent = accent, onToggle = { gradient = it })
                    KCChip(label = "Value labels", active = valueLabels, accent = accent, onToggle = { valueLabels = it })
                    KCChip(label = "Ref line", active = refLine, accent = accent, onToggle = { refLine = it })
                    KCChip(label = "Multi-series", active = multiSeries, accent = accent, onToggle = { multiSeries = it })
                    KCChip(label = "Stacked", active = stacked, accent = accent, onToggle = { stacked = it })
                    KCChip(label = "Horizontal", active = horizontal, accent = accent, onToggle = { horizontal = it })
                    KCChip(label = "Tooltip", active = tooltip, accent = accent, onToggle = { tooltip = it })
                    KCChip(label = "Crosshair", active = crosshair, accent = accent, onToggle = { crosshair = it })
                    KCChip(label = "Stroke", active = stroke, accent = accent, onToggle = { stroke = it })
                }
            }

            // ── USE CASE ──
            Column {
                KCSectionLabel("Use Case")
                Text(
                    text = "Compare discrete categories side by side. Each bar\u2019s height maps directly to its value, so differences read at a glance.",
                    modifier = Modifier.padding(top = AppDimen.Spacing_8dp),
                    color = AppColors.Body,
                    fontSize = AppTypography.Body,
                    lineHeight = AppTypography.descriptionLineHeight,
                )
                Text(
                    text = "Best for revenue by month, votes per option, or any countable comparison.",
                    modifier = Modifier.padding(top = AppDimen.Spacing_8dp),
                    color = AppColors.TextMuted,
                    fontSize = AppTypography.Description,
                    lineHeight = AppTypography.descriptionLineHeight,
                )
            }

            // ── COMPOSE ──
            KCCodeBlock(
                label = "Compose",
                accent = accent,
                code = codeSnippet,
            )

            // ── SAMPLE DATA ──
            Column {
                KCSectionLabel("Sample Data")
                KCDataTable(
                    modifier = Modifier.padding(top = AppDimen.Spacing_8dp),
                    head0 = "Month",
                    head1 = "Revenue",
                    rows = tableRows,
                )
            }
        }
    }
}
