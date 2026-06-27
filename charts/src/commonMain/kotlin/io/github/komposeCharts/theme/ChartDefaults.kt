package io.github.komposeCharts.theme

import androidx.compose.ui.graphics.Color

/**
 * Default values and palettes for chart theming.
 */
object ChartDefaults {

    /**
     * Okabe-Ito colorblind-safe palette (8 colors).
     * Widely recommended for data visualization accessibility.
     */
    val OkabeItoPalette: List<Color> = listOf(
        Color(0xFFE69F00), // orange
        Color(0xFF56B4E9), // sky blue
        Color(0xFF009E73), // bluish green
        Color(0xFFF0E442), // yellow
        Color(0xFF0072B2), // blue
        Color(0xFFD55E00), // vermilion
        Color(0xFFCC79A7), // reddish purple
        Color(0xFF000000), // black
    )

    /** Returns a [ChartTheme] with the Okabe-Ito palette and sensible defaults. */
    fun theme(): ChartTheme = ChartTheme(colors = OkabeItoPalette)
}
