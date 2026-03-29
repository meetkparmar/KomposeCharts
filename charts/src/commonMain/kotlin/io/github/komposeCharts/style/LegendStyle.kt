package io.github.komposeCharts.style

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Controls where the legend is placed relative to the chart plot. */
enum class LegendPosition { TOP, BOTTOM, LEFT, RIGHT }

/** Controls the layout direction of legend items. */
enum class LegendOrientation { HORIZONTAL, VERTICAL }

/**
 * Visual and layout configuration for chart legends.
 *
 * @param visible Whether the legend is shown at all.
 * @param position Where the legend appears relative to the chart.
 * @param orientation Direction items are arranged (horizontal row or vertical column).
 *   When [position] is [LegendPosition.LEFT] or [LegendPosition.RIGHT] and this is
 *   [LegendOrientation.HORIZONTAL], it is automatically coerced to [LegendOrientation.VERTICAL].
 * @param itemSpacing Gap between adjacent legend items.
 * @param swatchSize Diameter of the color swatch circle.
 */
data class LegendStyle(
    val visible: Boolean = true,
    val position: LegendPosition = LegendPosition.BOTTOM,
    val orientation: LegendOrientation = LegendOrientation.HORIZONTAL,
    val itemSpacing: Dp = 16.dp,
    val swatchSize: Dp = 10.dp,
)
