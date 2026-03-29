package io.github.komposeCharts.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.style.LegendOrientation
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.theme.ChartTheme
import io.github.komposeCharts.theme.LocalChartTheme

/**
 * Renders a legend showing series color swatches and labels.
 *
 * Layout direction is controlled by [legendStyle].orientation:
 * - [LegendOrientation.HORIZONTAL]: items in a Row
 * - [LegendOrientation.VERTICAL]: items in a Column
 *
 * Hidden when [legendStyle].visible is false or [series] is empty.
 */
@Composable
internal fun ChartLegend(
    series: List<DataSeries>,
    colors: List<Color>,
    legendStyle: LegendStyle = LegendStyle(),
    modifier: Modifier = Modifier,
    theme: ChartTheme = LocalChartTheme.current,
) {
    if (!legendStyle.visible || series.isEmpty()) return

    if (legendStyle.orientation == LegendOrientation.HORIZONTAL) {
        Row(
            modifier = modifier.wrapContentWidth(),
            horizontalArrangement = Arrangement.spacedBy(legendStyle.itemSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            series.forEachIndexed { i, s ->
                val color = colors.getOrElse(i) { theme.colors[i % theme.colors.size] }
                LegendItem(label = s.label, color = color, swatchSize = legendStyle.swatchSize, theme = theme)
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(legendStyle.itemSpacing),
        ) {
            series.forEachIndexed { i, s ->
                val color = colors.getOrElse(i) { theme.colors[i % theme.colors.size] }
                LegendItem(label = s.label, color = color, swatchSize = legendStyle.swatchSize, theme = theme)
            }
        }
    }
}

@Composable
private fun LegendItem(
    label: String,
    color: Color,
    swatchSize: androidx.compose.ui.unit.Dp = 10.dp,
    theme: ChartTheme,
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(swatchSize)
                .background(color = color, shape = CircleShape)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = label, style = theme.legendTextStyle)
    }
}
