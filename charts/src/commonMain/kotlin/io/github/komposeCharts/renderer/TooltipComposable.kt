package io.github.komposeCharts.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import io.github.komposeCharts.style.TooltipStyle

/**
 * A floating tooltip popup for displaying data point details.
 */
@Composable
internal fun ChartTooltip(
    text: String,
    style: TooltipStyle,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .background(
                color = style.backgroundColor,
                shape = RoundedCornerShape(style.cornerRadius),
            )
            .padding(style.padding),
    ) {
        Text(
            text = text,
            style = TextStyle(color = style.textColor),
        )
    }
}
