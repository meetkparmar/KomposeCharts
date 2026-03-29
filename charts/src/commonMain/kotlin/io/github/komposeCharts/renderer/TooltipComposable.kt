package io.github.komposeCharts.renderer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.layout.layout
import io.github.komposeCharts.interaction.TooltipState
import io.github.komposeCharts.style.TooltipStyle
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Floating data callout that appears near the last tapped element.
 *
 * Renders only when [state].isVisible is true. Positioned via [state].tapOffset
 * (canvas-relative coordinates). Auto-dismisses after [TooltipStyle.dismissAfterMs] ms.
 *
 * Note: Tooltip is positioned at the tap offset relative to the enclosing Box.
 * For BOTTOM/RIGHT legend layouts this is exact; for TOP/LEFT layouts there may be
 * a minor offset equal to the legend height/width (TODO: improve in v0.3.0).
 */
@Composable
internal fun ChartTooltip(
    state: TooltipState,
    style: TooltipStyle,
) {
    if (!state.isVisible) return
    val point = state.point ?: return

    if (style.dismissAfterMs > 0L) {
        LaunchedEffect(state.tapOffset, point) {
            delay(style.dismissAfterMs)
            state.dismiss()
        }
    }

    val labelText = point.label ?: "x=${point.x.toInt()}"
    val valueText = if (point.y == kotlin.math.floor(point.y.toDouble()).toFloat()) {
        point.y.toInt().toString()
    } else {
        (kotlin.math.round(point.y * 10f) / 10f).toString()
    }

    Column(
        modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width, placeable.height) {
                    // Position above the tap point; clamp so it doesn't clip off top
                    val xPx = state.tapOffset.x.roundToInt()
                    val yPx = (state.tapOffset.y.roundToInt() - placeable.height - 8).coerceAtLeast(0)
                    placeable.place(xPx, yPx)
                }
            }
            .background(
                color = Color.Black.copy(alpha = style.backgroundAlpha),
                shape = RoundedCornerShape(style.cornerRadius),
            )
            .padding(style.padding),
    ) {
        if (state.seriesLabel.isNotBlank()) {
            Text(
                text = state.seriesLabel,
                style = style.textStyle.copy(color = Color.White.copy(alpha = 0.7f)),
            )
        }
        Text(
            text = "$labelText: $valueText",
            style = style.textStyle.copy(color = Color.White),
        )
    }
}
