package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.internal.formatChartValue
import io.github.komposeCharts.style.AxisStyle
import io.github.komposeCharts.theme.ChartTheme

/**
 * Draws axis lines and tick labels.
 *
 * In the default (vertical) layout, [yTicks] are value ticks drawn down the left axis and
 * [xTicks]/[xLabels] are categories drawn along the bottom axis. When [swapAxes] is true
 * (horizontal bar charts), the axes are swapped: [yTicks] value labels are drawn along the
 * bottom axis, and [xLabels] category labels are drawn down the left axis — positioned to match
 * the renderer's top-to-bottom category layout using [categoryCount].
 */
internal fun DrawScope.drawAxes(
    xTicks: List<Float>,
    yTicks: List<Float>,
    mapper: ChartCoordinateMapper,
    style: AxisStyle,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    xLabels: List<String?>? = null,
    swapAxes: Boolean = false,
    categoryCount: Int = 0,
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

    if (swapAxes) {
        drawSwappedAxisLabels(
            valueTicks = yTicks,
            categoryLabels = xLabels,
            categoryCount = categoryCount,
            mapper = mapper,
            style = style,
            theme = theme,
            textMeasurer = textMeasurer,
        )
        return
    }

    drawYLabels(yTicks, mapper, style, theme, textMeasurer)
    drawXLabels(xTicks, xLabels, mapper, style, theme, textMeasurer)

}

/**
 * Draws axis labels for a horizontal (swapped) bar chart: value ticks along the bottom axis and
 * category labels down the left axis.
 *
 * Category labels are positioned to match the renderer's manual top-to-bottom category layout
 * (`plotTop + (i + 0.5) * plotHeight / categoryCount`), rather than the mapper's flipped y-axis.
 */
private fun DrawScope.drawSwappedAxisLabels(
    valueTicks: List<Float>,
    categoryLabels: List<String?>?,
    categoryCount: Int,
    mapper: ChartCoordinateMapper,
    style: AxisStyle,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
) {
    // Value tick labels along the bottom (x) axis.
    if (style.showXLabels) {
        val formatter = style.yLabelFormatter ?: ::formatChartValue
        for (tick in valueTicks) {
            val x = mapper.xToPixel(tick)
            if (x !in mapper.plotLeft..mapper.plotRight) continue
            val result = textMeasurer.measure(formatter(tick), style = theme.labelTextStyle)
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = x - result.size.width / 2f,
                    y = mapper.plotBottom + 4f,
                ),
            )
        }
    }

    // Category labels down the left (y) axis, matching the renderer's top-to-bottom layout.
    if (style.showYLabels && categoryLabels != null && categoryCount > 0) {
        val bandHeight = mapper.plotHeight / categoryCount
        for ((i, label) in categoryLabels.withIndex()) {
            if (label == null || i >= categoryCount) continue
            val centerY = mapper.plotTop + (i + 0.5f) * bandHeight
            val result = textMeasurer.measure(label, style = theme.labelTextStyle)
            drawText(
                textLayoutResult = result,
                topLeft = Offset(
                    x = mapper.plotLeft - result.size.width - 4f,
                    y = centerY - result.size.height / 2f,
                ),
            )
        }
    }
}

private fun DrawScope.drawYLabels(
    yTicks: List<Float>, mapper: ChartCoordinateMapper, style: AxisStyle,
    theme: ChartTheme, textMeasurer: TextMeasurer,
) {
    // Y-axis tick labels
    if (style.showYLabels) {
        val formatter = style.yLabelFormatter ?: ::formatChartValue
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

}

private fun DrawScope.drawXLabels(
    xTicks: List<Float>, xLabels: List<String?>?, mapper: ChartCoordinateMapper,
    style: AxisStyle, theme: ChartTheme, textMeasurer: TextMeasurer,
) {
    // X-axis tick labels
    if (style.showXLabels) {
        val formatter = style.xLabelFormatter
        for ((i, tick) in xTicks.withIndex()) {
            val x = mapper.xToPixel(tick)
            val text = when {
                xLabels != null && i < xLabels.size && xLabels[i] != null -> xLabels[i]!!
                formatter != null -> formatter(tick)
                else -> null
            }
            if (text == null || x !in mapper.plotLeft..mapper.plotRight) continue
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
