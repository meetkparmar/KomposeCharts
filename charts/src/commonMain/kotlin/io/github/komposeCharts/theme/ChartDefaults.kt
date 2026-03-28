package io.github.komposeCharts.theme

import androidx.compose.ui.graphics.Color

/**
 * Factory for default [ChartTheme] values.
 */
object ChartDefaults {

    /**
     * Okabe-Ito colorblind-safe 8-color palette.
     * Reference: https://jfly.uni-koeln.de/color/
     */
    val OkabeItoPalette: List<Color> = listOf(
        Color(0xFF0072B2), // Blue
        Color(0xFFE69F00), // Orange
        Color(0xFF009E73), // Green
        Color(0xFFF0E442), // Yellow
        Color(0xFF56B4E9), // Sky blue
        Color(0xFFD55E00), // Vermillion
        Color(0xFFCC79A7), // Reddish purple
        Color(0xFF000000), // Black
    )

    /** Returns the default [ChartTheme] using the Okabe-Ito palette. */
    fun theme(): ChartTheme = ChartTheme(colors = OkabeItoPalette)
}
