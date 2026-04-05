package io.github.komposeCharts.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

/**
 * Derives a [ChartTheme] from the ambient Material3 [MaterialTheme.colorScheme].
 *
 * Uses the primary, secondary, tertiary, and their container variants as the chart
 * color palette, keeping visual consistency with the app's branding.
 *
 * ```
 * ChartTheme(theme = chartThemeFromMaterial3()) {
 *     LineChart(data = data)
 * }
 * ```
 */
@Composable
fun chartThemeFromMaterial3(): ChartTheme {
    val scheme = MaterialTheme.colorScheme
    val colors = listOf(
        scheme.primary,
        scheme.tertiary,
        scheme.secondary,
        scheme.primaryContainer,
        scheme.tertiaryContainer,
        scheme.secondaryContainer,
        scheme.error,
        scheme.outline,
    )
    return ChartDefaults.darkTheme().copy(
        colors = colors,
        gridLineColor = scheme.outlineVariant.copy(alpha = 0.15f),
        axisLineColor = scheme.outlineVariant.copy(alpha = 0.4f),
    )
}
