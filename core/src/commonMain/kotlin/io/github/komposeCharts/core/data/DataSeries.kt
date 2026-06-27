package io.github.komposeCharts.core.data

/**
 * A named series of data points.
 *
 * @param label Display name for the series (used in legends).
 * @param points Ordered list of data points in this series.
 * @param colorToken Optional index into the theme palette to force a specific color.
 *                   When null, the series index is used instead.
 */
data class DataSeries(
    val label: String,
    val points: List<DataPoint>,
    val colorToken: Int? = null,
)
