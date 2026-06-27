package io.github.komposeCharts.core.data

/**
 * A single data point in a chart.
 *
 * @param x The x-axis value (category index for bar charts, continuous value for line charts).
 * @param y The y-axis value.
 * @param label Optional human-readable label (e.g. "Jan", "Q1").
 */
data class DataPoint(
    val x: Float,
    val y: Float,
    val label: String? = null,
) {
    /** Whether this point represents missing/null data. */
    val isMissing: Boolean get() = y.isNaN()

    companion object {
        /** Create a placeholder for missing data at the given x position. */
        fun missing(x: Float, label: String? = null) = DataPoint(x, Float.NaN, label)
    }
}
