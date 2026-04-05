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

    /**
     * Analytical Gallery dark-mode palette — luminous data colors optimized for
     * the dark teal-neutral surface defined in DesignSystem.md.
     */
    val AnalyticalGalleryPalette: List<Color> = listOf(
        Color(0xFF4FC3DC), // Light teal (primary)
        Color(0xFFA1E4FE), // Sky (tertiary)
        Color(0xFF8FCDD9), // Secondary teal
        Color(0xFFFFB74D), // Amber
        Color(0xFF81C784), // Soft green
        Color(0xFFFF8A65), // Coral
        Color(0xFFCE93D8), // Lavender
        Color(0xFFB0BEC5), // Cool gray
    )

    /** Returns the default [ChartTheme] using the Okabe-Ito palette. */
    fun theme(): ChartTheme = ChartTheme(colors = OkabeItoPalette)

    /** Returns a [ChartTheme] tuned for the Analytical Gallery dark design system. */
    fun darkTheme(): ChartTheme = ChartTheme(
        colors = AnalyticalGalleryPalette,
        gridLineColor = Color(0x1AFFFFFF),
        axisLineColor = Color(0x33FFFFFF),
    )
}
