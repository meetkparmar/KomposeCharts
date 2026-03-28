package io.github.komposeCharts.core.data

/**
 * A named sequence of data points forming one series in a chart.
 *
 * @param label Display name for this series (shown in legend).
 * @param points Ordered list of data points.
 * @param colorToken Optional index into the theme's color palette. Null = auto-assigned in order.
 */
data class DataSeries(
    val label: String,
    val points: List<DataPoint>,
    val colorToken: Int? = null,
)
