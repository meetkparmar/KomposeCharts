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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.charts.LineChart
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.style.CurveType
import io.github.komposeCharts.style.LegendPosition
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.LineChartStyle
import io.github.komposeCharts.style.TooltipStyle
import kotlin.random.Random

@Composable
fun LineChartScreen() {
    var showArea by remember { mutableStateOf(false) }
    var curveType by remember { mutableStateOf(CurveType.CATMULL_ROM) }
    var legendPosition by remember { mutableStateOf(LegendPosition.BOTTOM) }
    var dataRevision by remember { mutableStateOf(0) }
    var lastTapped by remember { mutableStateOf<String?>(null) }

    val data = remember(dataRevision) {
        ChartData(
            series = listOf(
                DataSeries(
                    label = "Revenue",
                    points = months.mapIndexed { i, m ->
                        DataPoint(i.toFloat(), Random.nextFloat() * 80f + 20f, m)
                    }
                ),
                DataSeries(
                    label = "Expenses",
                    points = months.mapIndexed { i, m ->
                        DataPoint(i.toFloat(), Random.nextFloat() * 50f + 10f, m)
                    }
                ),
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text("Line Chart", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        LineChart(
            data = data,
            modifier = Modifier.fillMaxWidth().height(260.dp),
            style = LineChartStyle(
                curveType = curveType,
                showArea = showArea,
                legendStyle = LegendStyle(position = legendPosition),
                tooltipStyle = TooltipStyle(),
            ),
            onDataPointClick = { sIdx, point ->
                lastTapped = "${data.series[sIdx].label}: ${point.label} = ${"%.1f".format(point.y)}"
            }
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = lastTapped ?: "Tap a data point to see data",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = curveType == CurveType.STRAIGHT,
                onClick = { curveType = CurveType.STRAIGHT },
                label = { Text("Straight") }
            )
            FilterChip(
                selected = curveType == CurveType.BEZIER,
                onClick = { curveType = CurveType.BEZIER },
                label = { Text("Bezier") }
            )
            FilterChip(
                selected = curveType == CurveType.CATMULL_ROM,
                onClick = { curveType = CurveType.CATMULL_ROM },
                label = { Text("Catmull-Rom") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = showArea,
                onClick = { showArea = !showArea },
                label = { Text("Area Fill") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text("Legend Position", style = MaterialTheme.typography.labelMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = legendPosition == LegendPosition.BOTTOM,
                onClick = { legendPosition = LegendPosition.BOTTOM },
                label = { Text("Bottom") }
            )
            FilterChip(
                selected = legendPosition == LegendPosition.TOP,
                onClick = { legendPosition = LegendPosition.TOP },
                label = { Text("Top") }
            )
            FilterChip(
                selected = legendPosition == LegendPosition.RIGHT,
                onClick = { legendPosition = LegendPosition.RIGHT },
                label = { Text("Right") }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = { dataRevision++; lastTapped = null }) {
            Text("Randomize")
        }
    }
}

private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
