package io.github.komposeCharts.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf

/** CompositionLocal that provides the current [ChartTheme] down the tree. */
val LocalChartTheme = staticCompositionLocalOf { ChartDefaults.theme() }

/**
 * Applies [theme] to all charts within [content].
 *
 * Charts read the theme via [LocalChartTheme].current. Nest this composable to scope
 * a custom theme to a portion of your UI.
 *
 * ```
 * ChartTheme(theme = ChartDefaults.theme()) {
 *     LineChart(data = data)
 * }
 * ```
 */
@Composable
fun ChartTheme(
    theme: ChartTheme,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalChartTheme provides theme, content = content)
}
