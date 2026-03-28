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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.charts.PieChart
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.style.PieChartStyle
import io.github.komposeCharts.style.SliceLabelType
import kotlin.random.Random

@Composable
fun PieChartScreen() {
    var labelType by remember { mutableStateOf(SliceLabelType.PERCENT) }
    var innerRadius by remember { mutableStateOf(0f) }
    var dataRevision by remember { mutableStateOf(0) }

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Pie / Donut Chart", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        PieChart(
            data = data,
            modifier = Modifier.fillMaxWidth().height(300.dp),
            style = PieChartStyle(
                sliceLabelType = labelType,
                innerRadiusFraction = innerRadius,
                centerLabel = centerLabel,
            ),
            onSliceClick = { _, point ->
                println("Tapped slice: ${point.label} = ${point.y}")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text("Slice Labels", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(SliceLabelType.PERCENT, SliceLabelType.VALUE, SliceLabelType.LABEL, SliceLabelType.NONE)
                .forEach { type ->
                    FilterChip(
                        selected = labelType == type,
                        onClick = { labelType = type },
                        label = { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
        }

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { dataRevision++ }) {
            Text("Randomize")
        }
    }
}
