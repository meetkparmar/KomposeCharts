package io.github.komposeCharts.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.internal.ChartState
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.style.LegendPosition
import io.github.komposeCharts.style.TooltipStyle
import io.github.komposeCharts.theme.ChartTheme

@Composable
internal fun ChartFrame(
    modifier: Modifier,
    state: ChartState,
    labels: List<String>,
    colors: List<Color>,
    legendStyle: LegendStyle,
    tooltipStyle: TooltipStyle?,
    theme: ChartTheme,
    plot: @Composable () -> Unit,
) {
    val legend: @Composable () -> Unit = {
        ChartLegend(labels, colors, legendStyle, theme, state.hidden, state::toggle)
    }
    Column(modifier.background(theme.backgroundColor)) {
        if (legendStyle.position == LegendPosition.TOP) legend()
        Row(Modifier.fillMaxWidth().weight(1f), verticalAlignment = Alignment.CenterVertically) {
            if (legendStyle.position == LegendPosition.LEFT) legend()
            Box(Modifier.weight(1f)) {
                plot()
                val text = state.tooltip
                if (text != null && tooltipStyle != null) {
                    ChartTooltip(text, tooltipStyle, Modifier.align(Alignment.TopCenter).padding(top = 8.dp))
                }
            }
            if (legendStyle.position == LegendPosition.RIGHT) legend()
        }
        if (legendStyle.position == LegendPosition.BOTTOM) legend()
    }
}
