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
import io.github.komposeCharts.charts.PieChart
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
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.PieChartStyle
import io.github.komposeCharts.style.PieLabelType
import io.github.komposeCharts.style.SelectionStyle
import io.github.komposeCharts.style.TooltipStyle
import kotlin.math.roundToInt
import kotlin.random.Random

private val LABELS = listOf("Rent", "Food", "Transport", "Leisure", "Savings")
private val DEFAULT_VALUES = listOf(1200f, 600f, 300f, 250f, 450f)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PieChartScreen(
    statusBarDp: Int = 0,
    onBack: () -> Unit = {},
) {
    // ── Option states ──
    var donut by remember { mutableStateOf(false) }
    var animate by remember { mutableStateOf(true) }
    var labels by remember { mutableStateOf(true) }
    var names by remember { mutableStateOf(false) }
    var legend by remember { mutableStateOf(true) }
    var tooltip by remember { mutableStateOf(false) }

    var dataVersion by remember { mutableStateOf(0) }

    // ── Data ──
    val currentValues = remember(dataVersion) {
        if (dataVersion == 0) DEFAULT_VALUES
        else {
            val rng = Random(dataVersion)
            LABELS.map { (rng.nextFloat() * 1000 + 100).roundToInt().toFloat() }
        }
    }

    val data = remember(currentValues) {
        ChartData(
            listOf(
                DataSeries(
                    label = "Budget",
                    points = currentValues.mapIndexed { i, v -> DataPoint(i.toFloat(), v, LABELS[i]) },
                ),
            ),
        )
    }

    // ── Style ──
    val accent = AppColors.AccentDefault
    val style = PieChartStyle(
        sliceColors = AppColors.ChartCategorical,
        donut = donut,
        showLabels = labels,
        labelType = if (names) PieLabelType.NAME_PERCENT else PieLabelType.PERCENT,
        selectionStyle = SelectionStyle(),
        legendStyle = LegendStyle(interactive = legend),
        tooltipStyle = if (tooltip) TooltipStyle() else null,
        animation = if (animate) ChartAnimation.Default else ChartAnimation.None,
    )

    // ── Dynamic code snippet ──
    val codeSnippet = remember(currentValues, donut, labels, names, legend, tooltip) {
        buildString {
            appendLine("PieChart(")
            appendLine("    data = ChartData(")
            appendLine("        listOf(")
            currentValues.forEachIndexed { i, v ->
                appendLine("            DataPoint(${i}f, ${v.roundToInt()}f, \"${LABELS[i]}\"),")
            }
            appendLine("        ),")
            appendLine("    ),")
            appendLine("    modifier = Modifier")
            appendLine("        .fillMaxWidth()")
            appendLine("        .height(260.dp),")
            appendLine("    style = PieChartStyle(")
            if (donut) appendLine("        donut = true,")
            if (labels) appendLine("        labelType = ${if (names) "NAME_PERCENT" else "PERCENT"},")
            if (!labels) appendLine("        showLabels = false,")
            if (legend) appendLine("        legendStyle = LegendStyle(interactive = true),")
            if (tooltip) appendLine("        tooltipStyle = TooltipStyle(),")
            appendLine("    ),")
            append(")")
        }
    }

    // ── Dynamic table rows ──
    val tableRows = remember(currentValues) {
        currentValues.mapIndexed { i, v -> KCTableRow(LABELS[i], "\$${v.roundToInt()}") }
    }

    // ── Layout ──
    Column(modifier = Modifier.fillMaxSize()) {
        KCTopBar(
            title = "Pie Chart",
            trailing = "PieChart()",
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
                PieChart(
                    data = data,
                    modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_260dp),
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
                    KCChip(label = "Donut", active = donut, accent = accent, onToggle = { donut = it })
                    KCChip(label = "Animate", active = animate, accent = accent, onToggle = {
                        animate = it; if (it) dataVersion++
                    })
                    KCChip(label = "Labels", active = labels, accent = accent, onToggle = { labels = it })
                    KCChip(label = "Names", active = names, accent = accent, onToggle = { names = it })
                    KCChip(label = "Legend", active = legend, accent = accent, onToggle = { legend = it })
                    KCChip(label = "Tooltip", active = tooltip, accent = accent, onToggle = { tooltip = it })
                }
            }

            Column {
                KCSectionLabel("Use Case")
                Text(
                    text = "Show how parts make up a whole. Each slice's angle is its share of the total, so dominant and minor categories stand out immediately.",
                    modifier = Modifier.padding(top = AppDimen.Spacing_8dp),
                    color = AppColors.Body,
                    fontSize = AppTypography.Body,
                    lineHeight = AppTypography.descriptionLineHeight,
                )
                Text(
                    text = "Best for budget breakdowns, market share, or any composition of a fixed total.",
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
                    head0 = "Category",
                    head1 = "Amount",
                    rows = tableRows,
                )
            }
        }
    }
}
