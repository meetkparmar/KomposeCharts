package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.internal.ChartHit
import io.github.komposeCharts.internal.barGeometry
import io.github.komposeCharts.internal.formatChartValue
import io.github.komposeCharts.style.BarChartStyle
import io.github.komposeCharts.style.BarOrientation
import io.github.komposeCharts.style.ValueLabelPosition
import io.github.komposeCharts.theme.ChartTheme

internal fun DrawScope.drawBars(
    data: ChartData,
    mapper: ChartCoordinateMapper,
    style: BarChartStyle,
    fractions: List<Float>,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    selectedBar: Pair<Int, Int>? = null,
): List<ChartHit> {
    val hits = barGeometry(data, mapper, style, fractions, theme, style.minBarHeight.toPx())
    val corners = CornerRadius(style.barCornerRadius.toPx())
    val vertical = style.orientation == BarOrientation.VERTICAL
    for (hit in hits) {
        val selection = style.selectionStyle
        val alpha = when {
            selection == null || selectedBar == null -> 1f
            selectedBar == hit.selection -> selection.highlightAlpha
            else -> selection.dimAlpha
        }
        drawSingleBar(hit.bounds, hit.color, alpha, style.barGradients?.getOrNull(hit.seriesIndex), style, corners, vertical)
        drawValueLabel(hit.displayValue, hit.bounds, style, theme, textMeasurer, vertical)
        if (selectedBar == hit.selection && selection != null) {
            val path = Path().apply { addRoundRect(RoundRect(hit.bounds, corners)) }
            drawPath(path, selection.highlightStrokeColor ?: hit.color, style = Stroke(selection.highlightStrokeWidth.toPx()))
        }
    }
    return hits
}

private fun DrawScope.drawSingleBar(
    rect: Rect,
    color: Color,
    alpha: Float,
    gradient: io.github.komposeCharts.style.BarGradient?,
    style: BarChartStyle,
    cornerRadius: CornerRadius,
    isVertical: Boolean,
) {
    if (rect.width <= 0f || rect.height <= 0f) return

    val path = Path().apply {
        addRoundRect(RoundRect(rect, cornerRadius))
    }

    // Fill
    if (gradient != null) {
        val brush = if (isVertical) {
            Brush.linearGradient(
                colors = listOf(gradient.topColor, gradient.bottomColor),
                start = Offset(rect.left, rect.top),
                end = Offset(rect.left, rect.bottom),
            )
        } else {
            Brush.linearGradient(
                colors = listOf(gradient.topColor, gradient.bottomColor),
                start = Offset(rect.left, rect.top),
                end = Offset(rect.right, rect.top),
            )
        }
        drawPath(path, brush, alpha = alpha)
    } else {
        drawPath(path, color, alpha = alpha)
    }

    // Stroke
    if (style.barStrokeWidth.toPx() > 0f) {
        val strokeColor = style.barStrokeColor ?: color.copy(alpha = 0.7f)
        drawPath(path, strokeColor, alpha = alpha, style = Stroke(width = style.barStrokeWidth.toPx()))
    }
}

private fun DrawScope.drawValueLabel(
    value: Float,
    barRect: Rect,
    style: BarChartStyle,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    isVertical: Boolean,
) {
    val position = style.valueLabelPosition
    if (position == ValueLabelPosition.NONE) return

    val formatter = style.valueLabelFormatter ?: ::formatChartValue
    val text = formatter(value)
    val result = textMeasurer.measure(text, style = theme.labelTextStyle)
    val textWidth = result.size.width.toFloat()
    val textHeight = result.size.height.toFloat()

    val topLeft = valueLabelOffset(barRect, position, textWidth, textHeight, isVertical)

    drawText(textLayoutResult = result, topLeft = topLeft)
}


private fun valueLabelOffset(
    barRect: Rect,
    position: ValueLabelPosition,
    textWidth: Float,
    textHeight: Float,
    isVertical: Boolean,
): Offset {
    return if (isVertical) {
        val x = barRect.center.x - textWidth / 2f
        val y = when (position) {
            ValueLabelPosition.TOP -> barRect.top - textHeight - 2f
            ValueLabelPosition.CENTER -> barRect.center.y - textHeight / 2f
            ValueLabelPosition.INSIDE_BOTTOM -> barRect.bottom - textHeight - 4f
            ValueLabelPosition.OUTSIDE -> barRect.top - textHeight - 8f
            ValueLabelPosition.NONE -> 0f
        }
        Offset(x, y)
    } else {
        val y = barRect.center.y - textHeight / 2f
        val x = when (position) {
            ValueLabelPosition.TOP -> barRect.right + 4f
            ValueLabelPosition.CENTER -> barRect.center.x - textWidth / 2f
            ValueLabelPosition.INSIDE_BOTTOM -> barRect.left + 4f
            ValueLabelPosition.OUTSIDE -> barRect.right + 8f
            ValueLabelPosition.NONE -> 0f
        }
        Offset(x, y)
    }

}
