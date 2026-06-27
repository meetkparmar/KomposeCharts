package io.github.komposeCharts.style

/**
 * Configuration for the chart legend.
 */
data class LegendStyle(
    val position: LegendPosition = LegendPosition.BOTTOM,
    val orientation: LegendOrientation = LegendOrientation.HORIZONTAL,
    /** When true, legend items are clickable to show/hide series. */
    val interactive: Boolean = false,
)
