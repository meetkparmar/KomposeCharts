package io.github.komposeCharts.sample.screen

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
import io.github.komposeCharts.style.LineChartStyle
import kotlin.random.Random

@Composable
fun LineChartScreen() {
    var showArea by remember { mutableStateOf(false) }
    var curveType by remember { mutableStateOf(CurveType.CATMULL_ROM) }
    var dataRevision by remember { mutableStateOf(0) }

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
            ),
            onDataPointClick = { sIdx, point ->
                println("Tapped series $sIdx: ${point.label} = ${point.y}")
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Controls
        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
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

        Row(horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = showArea,
                onClick = { showArea = !showArea },
                label = { Text("Area Fill") }
            )
            Button(onClick = { dataRevision++ }) {
                Text("Randomize")
            }
        }
    }
}

private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
