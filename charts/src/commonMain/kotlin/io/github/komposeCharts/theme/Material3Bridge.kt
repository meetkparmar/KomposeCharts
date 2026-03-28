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
        scheme.secondary,
        scheme.tertiary,
        scheme.primaryContainer,
        scheme.secondaryContainer,
        scheme.tertiaryContainer,
        scheme.error,
        scheme.outline,
    )
    return ChartDefaults.theme().copy(
        colors = colors,
        gridLineColor = scheme.outlineVariant.copy(alpha = 0.4f),
        axisLineColor = scheme.outline.copy(alpha = 0.6f),
    )
}
