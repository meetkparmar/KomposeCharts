package io.github.komposeCharts.core.data

/**
 * A single data point in a chart series.
 *
 * @param x Numeric x-axis value. For category charts (bar), use 0f, 1f, 2f... for index.
 * @param y Numeric y-axis value (the primary plotted value).
 * @param label Optional human-readable label for this position (shown on x-axis tick or tooltip).
 */
data class DataPoint(
    val x: Float,
    val y: Float,
    val label: String? = null,
)
