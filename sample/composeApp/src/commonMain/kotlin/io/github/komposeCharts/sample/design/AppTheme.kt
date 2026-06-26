package io.github.komposeCharts.sample.design

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Root theme composable for the KomposeCharts sample app.
 *
 * Applies the Analytical Gallery color scheme (light or dark) and wires
 * [ChartTheme] so all chart composables automatically pick up brand colors.
 *
 * @param darkTheme Whether to use the dark color scheme. Defaults to the system setting.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) AnalyticalGalleryDarkColorScheme else AnalyticalGalleryLightColorScheme
    MaterialTheme(colorScheme = colorScheme) {

    }
}
