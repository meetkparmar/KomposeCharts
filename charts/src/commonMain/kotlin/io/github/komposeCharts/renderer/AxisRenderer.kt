package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.style.AxisStyle
import io.github.komposeCharts.theme.ChartTheme
import kotlin.math.floor
import kotlin.math.round

/**
 * Draws x/y axis lines and tick labels onto the [DrawScope] canvas.
 * DrawScope implements Density, so .dp.toPx() works directly here.
 */
internal fun DrawScope.drawAxes(
    mapper: ChartCoordinateMapper,
    yTicks: List<Float>,
    xSeries: List<DataSeries>,
    style: AxisStyle,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
) {
    val axisStrokePx = style.axisLineThicknessDp.toPx()

    if (style.showYAxis) {
        drawLine(
            color = theme.axisLineColor,
            start = Offset(mapper.plotLeft, mapper.plotTop),
            end = Offset(mapper.plotLeft, mapper.plotBottom),
            strokeWidth = axisStrokePx,
        )
    }

    if (style.showXAxis) {
        drawLine(
            color = theme.axisLineColor,
            start = Offset(mapper.plotLeft, mapper.baselineY),
            end = Offset(mapper.plotRight, mapper.baselineY),
            strokeWidth = axisStrokePx,
        )
    }

    if (style.showYLabels) {
        val formatter = style.yLabelFormatter ?: ::formatAxisLabel
        for (tick in yTicks) {
            val y = mapper.yToPixel(tick)
            val rawLabel = formatter(tick)
            val label = if (style.labelAllCaps) rawLabel.uppercase() else rawLabel
            val measured = textMeasurer.measure(label, style = theme.labelTextStyle)
            val textX = (mapper.plotLeft - measured.size.width - 4.dp.toPx()).coerceAtLeast(0f)
            val textY = y - measured.size.height / 2f
            drawText(textLayoutResult = measured, topLeft = Offset(textX, textY))
        }
    }

    if (style.showXLabels && xSeries.isNotEmpty()) {
        val points = xSeries[0].points
        val formatter = style.xLabelFormatter
        for (point in points) {
            val rawLabel = formatter?.invoke(point.x) ?: point.label ?: continue
            val label = if (style.labelAllCaps) rawLabel.uppercase() else rawLabel
            val x = mapper.xToPixel(point.x)
            val measured = textMeasurer.measure(label, style = theme.labelTextStyle)
            val textX = x - measured.size.width / 2f
            val textY = mapper.baselineY + 4.dp.toPx()
            drawText(textLayoutResult = measured, topLeft = Offset(textX, textY))
        }
    }
}

private fun formatAxisLabel(value: Float): String =
    if (value == floor(value.toDouble()).toFloat()) value.toInt().toString()
    else (round(value * 10f) / 10f).toString()
