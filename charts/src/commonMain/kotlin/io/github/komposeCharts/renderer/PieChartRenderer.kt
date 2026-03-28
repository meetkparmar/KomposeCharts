package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import io.github.komposeCharts.core.data.DataSeries
import io.github.komposeCharts.style.PieChartStyle
import io.github.komposeCharts.style.SliceLabelType
import io.github.komposeCharts.theme.ChartTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

/**
 * Renders all pie/donut slices from [series] onto the DrawScope canvas.
 *
 * Animation: each slice's sweep angle is scaled by the overall [fraction] value.
 * Slices fan out clockwise from the start angle.
 */
internal fun DrawScope.drawPie(
    series: DataSeries,
    colors: List<Color>,
    style: PieChartStyle,
    fraction: Float,
    theme: ChartTheme,
    textMeasurer: TextMeasurer,
) {
    val points = series.points
    if (points.isEmpty()) return

    val total = points.sumOf { it.y.toDouble() }.toFloat()
    if (total <= 0f) return

    val cx = size.width / 2f
    val cy = size.height / 2f
    val radius = min(size.width, size.height) / 2f * 0.85f
    val innerRadius = radius * style.innerRadiusFraction

    val ovalSize = Size(radius * 2f, radius * 2f)
    val ovalTopLeft = Offset(cx - radius, cy - radius)

    var currentAngle = style.startAngleDeg

    points.forEachIndexed { i, point ->
        val color = colors.getOrElse(i) { theme.colors[i % theme.colors.size] }
        val sliceFraction = point.y / total
        val fullSweep = sliceFraction * 360f
        val animatedSweep = fullSweep * fraction
        val gapSweep = style.sliceSpacingDeg

        drawArc(
            color = color,
            startAngle = currentAngle + gapSweep / 2f,
            sweepAngle = (animatedSweep - gapSweep).coerceAtLeast(0f),
            useCenter = innerRadius == 0f,
            topLeft = ovalTopLeft,
            size = ovalSize,
        )

        // Slice label
        if (style.sliceLabelType != SliceLabelType.NONE && fraction > 0.7f && sliceFraction > 0.05f) {
            val labelAngleDeg = currentAngle + animatedSweep / 2f
            val labelAngleRad = PI / 180.0 * labelAngleDeg
            val labelRadius = (radius + innerRadius) / 2f
            val labelX = cx + labelRadius * cos(labelAngleRad).toFloat()
            val labelY = cy + labelRadius * sin(labelAngleRad).toFloat()

            val label = when (style.sliceLabelType) {
                SliceLabelType.PERCENT -> "${(sliceFraction * 100).toInt()}%"
                SliceLabelType.VALUE -> point.y.toInt().toString()
                SliceLabelType.LABEL -> point.label ?: ""
                SliceLabelType.NONE -> ""
            }
            if (label.isNotEmpty()) {
                val measured = textMeasurer.measure(label, style = theme.labelTextStyle)
                drawText(
                    textLayoutResult = measured,
                    topLeft = Offset(
                        labelX - measured.size.width / 2f,
                        labelY - measured.size.height / 2f,
                    ),
                )
            }
        }

        currentAngle += animatedSweep
    }

    // Donut hole — draw background circle to create the hole effect
    if (innerRadius > 0f && fraction > 0f) {
        drawCircle(
            color = theme.backgroundColor,
            radius = innerRadius,
            center = Offset(cx, cy),
        )

        // Center label
        val centerLabel = style.centerLabel
        if (centerLabel != null && fraction > 0.9f) {
            val measured = textMeasurer.measure(centerLabel, style = theme.titleTextStyle)
            drawText(
                textLayoutResult = measured,
                topLeft = Offset(
                    cx - measured.size.width / 2f,
                    cy - measured.size.height / 2f,
                ),
            )
        }
    }
}
