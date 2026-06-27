package io.github.komposeCharts.renderer

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import io.github.komposeCharts.internal.ChartCoordinateMapper
import io.github.komposeCharts.style.CrosshairStyle

/**
 * Draws crosshair guidelines at the given pointer position.
 */
internal fun DrawScope.drawCrosshair(
    pointerOffset: Offset,
    mapper: ChartCoordinateMapper,
    style: CrosshairStyle,
) {
    val pathEffect = style.dashPattern?.let { pattern ->
        if (pattern.size >= 2) PathEffect.dashPathEffect(pattern.toFloatArray(), 0f)
        else null
    }
    val strokeWidth = style.strokeWidth.toPx()

    if (style.showVertical && pointerOffset.x in mapper.plotLeft..mapper.plotRight) {
        drawLine(
            color = style.color,
            start = Offset(pointerOffset.x, mapper.plotTop),
            end = Offset(pointerOffset.x, mapper.plotBottom),
            strokeWidth = strokeWidth,
            pathEffect = pathEffect,
        )
    }

    if (style.showHorizontal && pointerOffset.y in mapper.plotTop..mapper.plotBottom) {
        drawLine(
            color = style.color,
            start = Offset(mapper.plotLeft, pointerOffset.y),
            end = Offset(mapper.plotRight, pointerOffset.y),
            strokeWidth = strokeWidth,
            pathEffect = pathEffect,
        )
    }
}
