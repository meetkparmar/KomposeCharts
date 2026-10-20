package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.core.charts.PieMath
import io.github.komposeCharts.core.data.DataPoint
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.style.PieChartStyle
import io.github.komposeCharts.style.PieLabelType
import io.github.komposeCharts.theme.ChartTheme
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.roundToInt
import kotlin.math.sin

private const val DegreesToRadians = (kotlin.math.PI / 180.0).toFloat()

/**
 * A rendered slice arc, retained for hit testing and selection.
 *
 * Angles are in degrees (clockwise from 3 o'clock; -90 = top). [center] is the
 * un-exploded pie center so hit testing is stable regardless of selection.
 */
internal data class SliceArc(
    val index: Int,
    val point: DataPoint,
    val startAngle: Float,
    val sweepAngle: Float,
    val center: Offset,
    val outerRadius: Float,
    val innerRadius: Float,
)

/** Resolves the fill color for the slice at [index]. */
internal fun pieSliceColor(index: Int, style: PieChartStyle, theme: ChartTheme): Color {
    style.sliceColors?.getOrNull(index)?.let { return it }
    val palette = theme.colors
    if (palette.isEmpty()) return Color.Gray
    return palette[index % palette.size]
}

/**
 * Renders the pie/donut slices of [series] and returns the slice arcs for hit testing.
 *
 * The entry animation is a clockwise reveal driven by [fraction] (0..1).
 *
 * @param hiddenSlices Indices to exclude (treated as zero, so others grow to fill).
 * @param selectedSlice Index of the exploded/highlighted slice, or null.
 */
internal fun DrawScope.drawPie(
    series: DataSeries,
    style: PieChartStyle,
    fraction: Float,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
    hiddenSlices: Set<Int>,
    selectedSlice: Int?,
): List<SliceArc> {
    val values = series.points.mapIndexed { i, p -> if (i in hiddenSlices || p.isMissing) 0f else p.y }
    val slices = PieMath.computeSlices(values, style.startAngle)
    val center = Offset(size.width / 2f, size.height / 2f)
    val outer = min(size.width, size.height) / 2f * 0.85f
    val inner = if (style.donut) outer * style.innerRadiusFraction.coerceIn(0f, 1f) else 0f
    val revealEnd = style.startAngle + 360f * fraction.coerceIn(0f, 1f)
    return slices.mapNotNull { slice ->
        val sweep = (revealEnd - slice.startAngle).coerceIn(0f, slice.sweepAngle)
        if (sweep <= 0f) null else {
            val arc = SliceArc(slice.index, series.points[slice.index], slice.startAngle, sweep, center, outer, inner)
            val angle = (slice.startAngle + slice.sweepAngle / 2f) * DegreesToRadians
            val drawCenter = if (selectedSlice == slice.index && style.selectionStyle != null) {
                center + Offset(cos(angle), sin(angle)) * 8.dp.toPx()
            } else center
            drawSlice(arc.copy(center = drawCenter), style, theme)
            if (style.showLabels && slice.sweepAngle >= 8f && sweep >= slice.sweepAngle) {
                drawSliceLabel(arc.copy(center = drawCenter), angle, slice.fraction, style, theme, textMeasurer)
            }
            arc
        }
    }
}

private fun DrawScope.drawSlice(arc: SliceArc, style: PieChartStyle, theme: ChartTheme) {
    val color = pieSliceColor(arc.index, style, theme)
    if (arc.innerRadius > 0f) {
        val radius = (arc.outerRadius + arc.innerRadius) / 2f
        drawArc(
            color, arc.startAngle, arc.sweepAngle, false,
            arc.center - Offset(radius, radius), Size(radius * 2f, radius * 2f),
            style = Stroke(arc.outerRadius - arc.innerRadius),
        )
    } else {
        val topLeft = arc.center - Offset(arc.outerRadius, arc.outerRadius)
        val size = Size(arc.outerRadius * 2f, arc.outerRadius * 2f)
        drawArc(color, arc.startAngle, arc.sweepAngle, true, topLeft, size)
        val stroke = style.sliceStrokeColor
        if (stroke != null && style.sliceStrokeWidth.toPx() > 0f) {
            drawArc(stroke, arc.startAngle, arc.sweepAngle, true, topLeft, size, style = Stroke(style.sliceStrokeWidth.toPx()))
        }
    }
}

private fun DrawScope.drawSliceLabel(
    arc: SliceArc,
    angle: Float,
    fraction: Float,
    style: PieChartStyle,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
) {
    val radius = if (arc.innerRadius > 0f) (arc.innerRadius + arc.outerRadius) / 2f else arc.outerRadius * 0.62f
    val text = pieLabelText(style.labelType, arc.point, fraction)
    if (text.isEmpty()) return
    val result = textMeasurer.measure(text, style = theme.labelTextStyle.copy(color = Color.White))
    val x = arc.center.x + cos(angle) * radius - result.size.width / 2f
    val y = arc.center.y + sin(angle) * radius - result.size.height / 2f
    drawText(result, topLeft = Offset(x, y))
}

private fun pieLabelText(type: PieLabelType, point: DataPoint, fraction: Float): String {
    val pct = "${(fraction * 100f).roundToInt()}%"
    val name = point.label ?: ""
    val value = io.github.komposeCharts.internal.formatChartValue(point.y)
    return when (type) {
        PieLabelType.PERCENT -> pct
        PieLabelType.VALUE -> value
        PieLabelType.NAME -> name
        PieLabelType.NAME_PERCENT -> if (name.isEmpty()) pct else "$name $pct"
    }
}
