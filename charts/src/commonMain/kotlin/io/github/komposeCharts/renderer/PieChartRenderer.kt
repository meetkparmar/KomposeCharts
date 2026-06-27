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

private const val DEG_TO_RAD = (kotlin.math.PI / 180.0).toFloat()

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
    val values = series.points.mapIndexed { i, p ->
        if (i in hiddenSlices || p.isMissing) 0f else p.y
    }
    val slices = PieMath.computeSlices(values, style.startAngle)
    if (slices.isEmpty()) return emptyList()

    val center = Offset(size.width / 2f, size.height / 2f)
    val outerRadius = min(size.width, size.height) / 2f * 0.85f
    val innerRadius = if (style.donut) outerRadius * style.innerRadiusFraction else 0f
    val explodePx = 8.dp.toPx()

    val revealEnd = style.startAngle + 360f * fraction
    val arcs = mutableListOf<SliceArc>()

    for (slice in slices) {
        if (slice.sweepAngle <= 0f) continue
        val point = series.points[slice.index]
        val drawnSweep = (revealEnd - slice.startAngle).coerceIn(0f, slice.sweepAngle)
        if (drawnSweep <= 0f) {
            arcs.add(SliceArc(slice.index, point, slice.startAngle, slice.sweepAngle, center, outerRadius, innerRadius))
            continue
        }

        val color = pieSliceColor(slice.index, style, theme)
        val midAngleRad = (slice.startAngle + slice.sweepAngle / 2f) * DEG_TO_RAD

        // Explode the selected slice outward along its mid-angle.
        val drawCenter = if (selectedSlice == slice.index && style.selectionStyle != null) {
            Offset(center.x + cos(midAngleRad) * explodePx, center.y + sin(midAngleRad) * explodePx)
        } else {
            center
        }

        if (innerRadius > 0f) {
            // Donut: stroke an arc along the mid-radius ring.
            val midRadius = (outerRadius + innerRadius) / 2f
            val ringWidth = outerRadius - innerRadius
            drawArc(
                color = color,
                startAngle = slice.startAngle,
                sweepAngle = drawnSweep,
                useCenter = false,
                topLeft = Offset(drawCenter.x - midRadius, drawCenter.y - midRadius),
                size = Size(midRadius * 2f, midRadius * 2f),
                style = Stroke(width = ringWidth),
            )
        } else {
            // Pie wedge.
            drawArc(
                color = color,
                startAngle = slice.startAngle,
                sweepAngle = drawnSweep,
                useCenter = true,
                topLeft = Offset(drawCenter.x - outerRadius, drawCenter.y - outerRadius),
                size = Size(outerRadius * 2f, outerRadius * 2f),
            )
            // Separator outline.
            val strokeColor = style.sliceStrokeColor
            if (strokeColor != null && style.sliceStrokeWidth.toPx() > 0f) {
                drawArc(
                    color = strokeColor,
                    startAngle = slice.startAngle,
                    sweepAngle = drawnSweep,
                    useCenter = true,
                    topLeft = Offset(drawCenter.x - outerRadius, drawCenter.y - outerRadius),
                    size = Size(outerRadius * 2f, outerRadius * 2f),
                    style = Stroke(width = style.sliceStrokeWidth.toPx()),
                )
            }
        }

        // Label (only once the slice is fully revealed and large enough to read).
        if (style.showLabels && slice.sweepAngle >= 8f && drawnSweep >= slice.sweepAngle) {
            val labelRadius = if (innerRadius > 0f) (innerRadius + outerRadius) / 2f else outerRadius * 0.62f
            val labelText = pieLabelText(style.labelType, point, slice.fraction)
            if (labelText.isNotEmpty()) {
                val result = textMeasurer.measure(labelText, style = theme.labelTextStyle.copy(color = Color.White))
                val lx = drawCenter.x + cos(midAngleRad) * labelRadius - result.size.width / 2f
                val ly = drawCenter.y + sin(midAngleRad) * labelRadius - result.size.height / 2f
                drawText(textLayoutResult = result, topLeft = Offset(lx, ly))
            }
        }

        arcs.add(SliceArc(slice.index, point, slice.startAngle, slice.sweepAngle, center, outerRadius, innerRadius))
    }

    return arcs
}

private fun pieLabelText(type: PieLabelType, point: DataPoint, fraction: Float): String {
    val pct = "${(fraction * 100f).roundToInt()}%"
    val name = point.label ?: ""
    val value = if (point.y == point.y.toLong().toFloat()) {
        point.y.toLong().toString()
    } else {
        "${(point.y * 10).toInt() / 10.0}"
    }
    return when (type) {
        PieLabelType.PERCENT -> pct
        PieLabelType.VALUE -> value
        PieLabelType.NAME -> name
        PieLabelType.NAME_PERCENT -> if (name.isEmpty()) pct else "$name $pct"
    }
}
