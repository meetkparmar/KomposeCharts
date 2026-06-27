package io.github.komposeCharts.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal that provides the current [ChartTheme] to chart composables.
 */
val LocalChartTheme = staticCompositionLocalOf { ChartDefaults.theme() }

/**
 * Applies the given [theme] to all chart composables within [content].
 *
 * Usage:
 * ```
 * ChartTheme(myTheme) {
 *     BarChart(data)
 * }
 * ```
 */
@Composable
fun ChartTheme(
    theme: ChartTheme = ChartDefaults.theme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalChartTheme provides theme, content = content)
}
