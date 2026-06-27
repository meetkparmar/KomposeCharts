package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.style.ReferenceLine
import io.github.komposeCharts.style.ReferenceLineLabelPosition
import io.github.komposeCharts.style.ReferenceLineOrientation
import io.github.komposeCharts.theme.ChartTheme

/**
 * Draws reference/threshold lines on the chart.
 */
internal fun DrawScope.drawReferenceLines(
    lines: List<ReferenceLine>,
    mapper: ChartCoordinateMapper,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
) {
    for (line in lines) {
        val pathEffect = line.dashPattern?.let { pattern ->
            if (pattern.size >= 2) PathEffect.dashPathEffect(pattern.toFloatArray(), 0f)
            else null
        }

        val strokeWidth = line.strokeWidth.toPx()

        if (line.orientation == ReferenceLineOrientation.HORIZONTAL) {
            val y = mapper.yToPixel(line.value)
            if (y !in mapper.plotTop..mapper.plotBottom) continue

            drawLine(
                color = line.color,
                start = Offset(mapper.plotLeft, y),
                end = Offset(mapper.plotRight, y),
                strokeWidth = strokeWidth,
                pathEffect = pathEffect,
            )

            line.label?.let { labelText ->
                val result = textMeasurer.measure(labelText, style = theme.labelTextStyle)
                val x = when (line.labelPosition) {
                    ReferenceLineLabelPosition.START -> mapper.plotLeft + 4f
                    ReferenceLineLabelPosition.END -> mapper.plotRight - result.size.width - 4f
                    ReferenceLineLabelPosition.CENTER -> (mapper.plotLeft + mapper.plotRight) / 2f - result.size.width / 2f
                }
                drawText(result, topLeft = Offset(x, y - result.size.height - 2f))
            }
        } else {
            val x = mapper.xToPixel(line.value)
            if (x !in mapper.plotLeft..mapper.plotRight) continue

            drawLine(
                color = line.color,
                start = Offset(x, mapper.plotTop),
                end = Offset(x, mapper.plotBottom),
                strokeWidth = strokeWidth,
                pathEffect = pathEffect,
            )

            line.label?.let { labelText ->
                val result = textMeasurer.measure(labelText, style = theme.labelTextStyle)
                val labelY = when (line.labelPosition) {
                    ReferenceLineLabelPosition.START -> mapper.plotTop + 4f
                    ReferenceLineLabelPosition.END -> mapper.plotBottom - result.size.height - 4f
                    ReferenceLineLabelPosition.CENTER -> (mapper.plotTop + mapper.plotBottom) / 2f - result.size.height / 2f
                }
                drawText(result, topLeft = Offset(x + 4f, labelY))
            }
        }
    }
}
