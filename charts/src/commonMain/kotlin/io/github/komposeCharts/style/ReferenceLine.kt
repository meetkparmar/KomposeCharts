package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * A reference or threshold line drawn at a specific data value.
 */
data class ReferenceLine(
    /** The data value where the line is drawn. */
    val value: Float,
    val orientation: ReferenceLineOrientation = ReferenceLineOrientation.HORIZONTAL,
    val color: Color = Color(0xFFFF6B6B),
    val strokeWidth: Dp = 1.5.dp,
    /** Dash pattern in pixels. Null = solid line. */
    val dashPattern: List<Float>? = listOf(8f, 4f),
    /** Optional text label next to the line. */
    val label: String? = null,
    val labelPosition: ReferenceLineLabelPosition = ReferenceLineLabelPosition.END,
)

enum class ReferenceLineOrientation { HORIZONTAL, VERTICAL }

enum class ReferenceLineLabelPosition { START, END, CENTER }
