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
    /**
     * Whether this point represents missing/null data or is otherwise unplottable.
     *
     * A point is considered missing if either coordinate is non-finite (NaN or
     * infinity). This guards axis-range and rendering math against values that
     * cannot be plotted (e.g. NaN from a gap, or infinity from a bad division).
     */
    val isMissing: Boolean get() = !x.isFinite() || !y.isFinite()

    companion object {
        /** Create a placeholder for missing data at the given x position. */
        fun missing(x: Float, label: String? = null) = DataPoint(x, Float.NaN, label)
    }
}
