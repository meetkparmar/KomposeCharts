package io.github.komposeCharts.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.style.LegendOrientation
import io.github.komposeCharts.style.LegendStyle
import io.github.komposeCharts.theme.ChartTheme

/**
 * Renders the chart legend with colored swatches and series labels.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ChartLegend(
    labels: List<String>,
    colors: List<Color>,
    style: LegendStyle,
    theme: ChartTheme,
    hiddenSeries: Set<Int> = emptySet(),
    onToggle: ((Int) -> Unit)? = null,
) {
    if (labels.isEmpty()) return

    val content: @Composable () -> Unit = {
        labels.forEachIndexed { index, label ->
            val isHidden = index in hiddenSeries
            val color = colors.getOrElse(index) { Color.Gray }
            val modifier = if (style.interactive && onToggle != null) {
                Modifier.clickable { onToggle(index) }
            } else {
                Modifier
            }

            Row(
                modifier = modifier
                    .padding(horizontal = 6.dp, vertical = 2.dp)
                    .then(if (isHidden) Modifier.alpha(0.4f) else Modifier),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .background(color, CircleShape),
                )
                Text(
                    text = label,
                    style = theme.legendTextStyle.copy(
                        textDecoration = if (isHidden) TextDecoration.LineThrough else null,
                    ),
                )
            }
        }
    }

    if (style.orientation == LegendOrientation.VERTICAL) {
        Column(
            modifier = Modifier.padding(4.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            content()
        }
    } else {
        FlowRow(
            modifier = Modifier.padding(4.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            content()
        }
    }
}
