package io.github.komposeCharts.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.Text
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.theme.ChartTheme
import io.github.komposeCharts.theme.LocalChartTheme

/** Renders a horizontal legend showing series color swatches and labels. */
@Composable
internal fun ChartLegend(
    series: List<DataSeries>,
    colors: List<Color>,
    modifier: Modifier = Modifier,
    theme: ChartTheme = LocalChartTheme.current,
) {
    if (series.size <= 1) return

    Row(
        modifier = modifier.fillMaxWidth().wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        series.forEachIndexed { i, s ->
            val color = colors.getOrElse(i) { theme.colors[i % theme.colors.size] }
            LegendItem(label = s.label, color = color, theme = theme)
        }
    }
}

@Composable
private fun LegendItem(label: String, color: Color, theme: ChartTheme) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = theme.legendTextStyle)
    }
}
