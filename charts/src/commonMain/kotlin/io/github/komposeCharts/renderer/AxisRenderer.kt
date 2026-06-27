package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.style.AxisStyle
import io.github.komposeCharts.theme.ChartTheme

/**
 * Draws axis lines and tick labels.
 */
internal fun DrawScope.drawAxes(
    xTicks: List<Float>,
    yTicks: List<Float>,
    mapper: ChartCoordinateMapper,
    style: AxisStyle,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    xLabels: List<String?>? = null,
) {
    val axisColor = theme.axisLineColor
    val axisStroke = style.axisLineThicknessDp.toPx()

    // Y axis line
    if (style.showYAxis) {
        drawLine(
            color = axisColor,
            start = Offset(mapper.plotLeft, mapper.plotTop),
            end = Offset(mapper.plotLeft, mapper.plotBottom),
            strokeWidth = axisStroke,
        )
    }

    // X axis line
    if (style.showXAxis) {
        drawLine(
            color = axisColor,
            start = Offset(mapper.plotLeft, mapper.plotBottom),
            end = Offset(mapper.plotRight, mapper.plotBottom),
            strokeWidth = axisStroke,
        )
    }

    // Y-axis tick labels
    if (style.showYLabels) {
        val formatter = style.yLabelFormatter ?: { v ->
            if (v == v.toLong().toFloat()) v.toLong().toString() else "${(v * 10).toInt() / 10.0}"
        }
        for (tick in yTicks) {
            val y = mapper.yToPixel(tick)
            if (y !in mapper.plotTop..mapper.plotBottom) continue
            val text = formatter(tick)
            val result = textMeasurer.measure(text, style = theme.labelTextStyle)
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = mapper.plotLeft - result.size.width - 4f,
                    y = y - result.size.height / 2f,
                ),
            )
        }
    }

    // X-axis tick labels
    if (style.showXLabels) {
        val formatter = style.xLabelFormatter
        for ((i, tick) in xTicks.withIndex()) {
            val x = mapper.xToPixel(tick)
            if (x !in mapper.plotLeft..mapper.plotRight) continue
            val text = when {
                xLabels != null && i < xLabels.size && xLabels[i] != null -> xLabels[i]!!
                formatter != null -> formatter(tick)
                else -> continue
            }
            val result = textMeasurer.measure(text, style = theme.labelTextStyle)
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x - result.size.width / 2f,
                    y = mapper.plotBottom + 4f,
                ),
            )
        }
    }
}
