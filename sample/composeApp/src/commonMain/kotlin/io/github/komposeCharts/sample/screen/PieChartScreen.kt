package io.github.komposeCharts.sample.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.komposeCharts.charts.PieChart
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.PieChartStyle
import io.github.komposeCharts.style.SliceLabelType
import io.github.komposeCharts.style.TooltipStyle
import kotlin.random.Random

@Composable
fun PieChartScreen() {
    var labelType by remember { mutableStateOf(SliceLabelType.PERCENT) }
    var innerRadius by remember { mutableStateOf(0f) }
    var showLegend by remember { mutableStateOf(true) }
    var dataRevision by remember { mutableStateOf(0) }
    var lastTapped by remember { mutableStateOf<String?>(null) }

    val categories = listOf("Design", "Engineering", "Marketing", "Sales", "Support")

    val data = remember(dataRevision) {
        ChartData(
            series = listOf(
                DataSeries(
                    label = "Budget",
                    points = categories.mapIndexed { i, cat ->
                        DataPoint(i.toFloat(), Random.nextFloat() * 40f + 10f, cat)
                    }
                )
            )
        )
    }

    val centerLabel = if (innerRadius > 0.1f) "Budget" else null
    val chipShape = RoundedCornerShape(AppDimen.Spacing_4dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppDimen.Spacing_24dp)
    ) {
        Text("Pie / Donut Chart", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(AppDimen.Spacing_16dp))

        PieChart(
            data = data,
            modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_300dp),
            style = PieChartStyle(
                sliceLabelType = labelType,
                innerRadiusFraction = innerRadius,
                centerLabel = centerLabel,
                legendStyle = LegendStyle(visible = showLegend),
                tooltipStyle = TooltipStyle(dismissAfterMs = 3000L),
            ),
            onSliceClick = { _, point ->
                lastTapped = "${point.label}: ${(kotlin.math.round(point.y * 10.0) / 10.0)}"
            }
        )

        Spacer(modifier = Modifier.height(AppDimen.Spacing_8dp))
        Text(
            text = lastTapped ?: "Tap a slice to see data",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(AppDimen.Spacing_16dp))

        Text("Slice Labels", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp)) {
            listOf(SliceLabelType.PERCENT, SliceLabelType.VALUE, SliceLabelType.LABEL, SliceLabelType.NONE)
                .forEach { type ->
                    FilterChip(
                        selected = labelType == type,
                        onClick = { labelType = type },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        shape = chipShape,
                    )
                }
        }

        Spacer(modifier = Modifier.height(AppDimen.Spacing_8dp))

        Text(
            "Inner radius (donut): ${(innerRadius * 100).toInt()}%",
            style = MaterialTheme.typography.labelMedium,
        )
        Slider(
            value = innerRadius,
            onValueChange = { innerRadius = it },
            valueRange = 0f..0.8f,
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(AppDimen.Spacing_8dp))

        FilterChip(
            selected = showLegend,
            onClick = { showLegend = !showLegend },
            label = { Text("Show Legend") },
            shape = chipShape,
        )

        Spacer(modifier = Modifier.height(AppDimen.Spacing_8dp))

        OutlinedButton(
            onClick = { dataRevision++; lastTapped = null },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(AppDimen.Spacing_24dp),
        ) {
            Text("Randomize Data")
        }
    }
}
