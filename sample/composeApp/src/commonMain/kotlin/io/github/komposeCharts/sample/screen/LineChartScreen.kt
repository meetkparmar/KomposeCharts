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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.github.komposeCharts.charts.LineChart
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.style.CurveType
import io.github.komposeCharts.style.LegendPosition
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.LineChartStyle
import io.github.komposeCharts.style.TooltipStyle
import kotlin.random.Random

@Composable
fun LineChartScreen(onViewDetail: () -> Unit = {}) {
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

    val chipShape = RoundedCornerShape(AppDimen.Spacing_4dp)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(AppDimen.Spacing_24dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        ) {
            Text("Line Chart", style = MaterialTheme.typography.titleLarge)
            Button(
                onClick = onViewDetail,
                shape = RoundedCornerShape(AppDimen.Spacing_24dp),
            ) {
                Icon(
                    Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.padding(end = AppDimen.Spacing_4dp),
                )
                Text("Details")
            }
        }
        Spacer(modifier = Modifier.height(AppDimen.Spacing_16dp))

        LineChart(
            data = data,
            modifier = Modifier.fillMaxWidth().height(AppDimen.Spacing_260dp),
            style = LineChartStyle(
                curveType = curveType,
                showArea = showArea,
                legendStyle = LegendStyle(position = legendPosition),
                tooltipStyle = TooltipStyle(),
            ),
            onDataPointClick = { sIdx, point ->
                lastTapped = "${data.series[sIdx].label}: ${point.label} = ${(kotlin.math.round(point.y * 10.0) / 10.0)}"
            }
        )

        Spacer(modifier = Modifier.height(AppDimen.Spacing_8dp))
        Text(
            text = lastTapped ?: "Tap a data point to see data",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Spacer(modifier = Modifier.height(AppDimen.Spacing_16dp))

        Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp)) {
            FilterChip(
                selected = curveType == CurveType.STRAIGHT,
                onClick = { curveType = CurveType.STRAIGHT },
                label = { Text("Straight") },
                shape = chipShape,
            )
            FilterChip(
                selected = curveType == CurveType.BEZIER,
                onClick = { curveType = CurveType.BEZIER },
                label = { Text("Bezier") },
                shape = chipShape,
            )
            FilterChip(
                selected = curveType == CurveType.CATMULL_ROM,
                onClick = { curveType = CurveType.CATMULL_ROM },
                label = { Text("Catmull-Rom") },
                shape = chipShape,
            )
        }

        Spacer(modifier = Modifier.height(AppDimen.Spacing_8dp))

        Row(horizontalArrangement = Arrangement.spacedBy(AppDimen.Spacing_8dp)) {
            FilterChip(
                selected = showArea,
                onClick = { showArea = !showArea },
                label = { Text("Area Fill") },
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

private val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
    "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
