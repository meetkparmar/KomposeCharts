package io.github.komposeCharts.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

/**
 * Creates a [ChartTheme] that derives its text styles from the current Material 3 theme.
 * Colors still use the Okabe-Ito palette for optimal data visualization accessibility.
 */
@Composable
fun chartThemeFromMaterial3(): ChartTheme {
    val typography = MaterialTheme.typography
    val colorScheme = MaterialTheme.colorScheme

    return ChartDefaults.theme().copy(
        backgroundColor = colorScheme.surface,
        gridLineColor = colorScheme.outlineVariant.copy(alpha = 0.3f),
        axisLineColor = colorScheme.outline.copy(alpha = 0.5f),
        labelTextStyle = TextStyle(
            fontSize = typography.labelSmall.fontSize,
            color = colorScheme.onSurfaceVariant,
        ),
        titleTextStyle = TextStyle(
            fontSize = typography.titleSmall.fontSize,
            color = colorScheme.onSurface,
        ),
        legendTextStyle = TextStyle(
            fontSize = typography.labelMedium.fontSize,
            color = colorScheme.onSurfaceVariant,
        ),
    )
}
