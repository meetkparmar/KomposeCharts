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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.komposeCharts.charts.BarChart
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.BarGrouping
import io.github.komposeCharts.style.BarOrientation
import io.github.komposeCharts.style.LegendPosition
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.TooltipStyle
import kotlin.random.Random

@Composable
fun BarChartScreen() {
    var grouping by remember { mutableStateOf(BarGrouping.GROUPED) }
    var orientation by remember { mutableStateOf(BarOrientation.VERTICAL) }
    var legendPosition by remember { mutableStateOf(LegendPosition.BOTTOM) }
    var dataRevision by remember { mutableStateOf(0) }
    var lastTapped by remember { mutableStateOf<String?>(null) }

    val quarters = listOf("Q1", "Q2", "Q3", "Q4")

    val data = remember(dataRevision) {
        ChartData(
            series = listOf(
                DataSeries(
                    label = "2023",
                    points = quarters.mapIndexed { i, q ->
                        DataPoint(i.toFloat(), Random.nextFloat() * 60f + 20f, q)
                    }
                ),
                DataSeries(
                    label = "2024",
                    points = quarters.mapIndexed { i, q ->
                        DataPoint(i.toFloat(), Random.nextFloat() * 60f + 20f, q)
                    }
                ),
            )
        )
    }

    val chipShape = RoundedCornerShape(AppDimen.Spacing_4dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppDimen.Spacing_24dp)
    ) {
        Text("Bar Chart", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(AppDimen.Spacing_16dp))

        BarChart(
            data = data,
            modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_260dp),
            style = BarChartStyle(
                grouping = grouping,
                orientation = orientation,
                showValueLabels = true,
                legendStyle = LegendStyle(position = legendPosition),
                tooltipStyle = TooltipStyle(),
            ),
            onBarClick = { sIdx, point ->
                lastTapped = "${data.series[sIdx].label}: ${point.label} = ${point.y.toInt()}"
            }
        )

        Spacer(modifier = Modifier.height(AppDimen.Spacing_8dp))
        Text(
            text = lastTapped ?: "Tap a bar to see data",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(AppDimen.Spacing_16dp))

        Text("Grouping", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp)) {
            FilterChip(
                selected = grouping == BarGrouping.GROUPED,
                onClick = { grouping = BarGrouping.GROUPED },
                label = { Text("Grouped") },
                shape = chipShape,
            )
            FilterChip(
                selected = grouping == BarGrouping.STACKED,
                onClick = { grouping = BarGrouping.STACKED },
                label = { Text("Stacked") },
                shape = chipShape,
            )
        }

        Spacer(modifier = Modifier.height(AppDimen.Spacing_8dp))

        Text("Orientation", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp)) {
            FilterChip(
                selected = orientation == BarOrientation.VERTICAL,
                onClick = { orientation = BarOrientation.VERTICAL },
                label = { Text("Vertical") },
                shape = chipShape,
            )
            FilterChip(
                selected = orientation == BarOrientation.HORIZONTAL,
                onClick = { orientation = BarOrientation.HORIZONTAL },
                label = { Text("Horizontal") },
                shape = chipShape,
            )
        }

        Spacer(modifier = Modifier.height(AppDimen.Spacing_8dp))

        Text("Legend Position", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp)) {
            FilterChip(
                selected = legendPosition == LegendPosition.BOTTOM,
                onClick = { legendPosition = LegendPosition.BOTTOM },
                label = { Text("Bottom") },
                shape = chipShape,
            )
            FilterChip(
                selected = legendPosition == LegendPosition.TOP,
                onClick = { legendPosition = LegendPosition.TOP },
                label = { Text("Top") },
                shape = chipShape,
            )
            FilterChip(
                selected = legendPosition == LegendPosition.RIGHT,
                onClick = { legendPosition = LegendPosition.RIGHT },
                label = { Text("Right") },
                shape = chipShape,
            )
        }

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
