package io.github.komposeCharts.core.data

/**
 * Top-level container for chart data, holding one or more [DataSeries].
 *
 * Provides convenience constructors for common use cases:
 * - From a list of [DataPoint]s (single series)
 * - From a list of Float values with auto-generated x indices
 * - From multiple [DataSeries] (multi-series)
 */
data class ChartData(val series: List<DataSeries>) {

    /** True when there are no data points across all series. */
    val isEmpty: Boolean get() = series.isEmpty() || series.all { it.points.isEmpty() }

    /** Single series from a list of [DataPoint]s. */
    constructor(points: List<DataPoint>, label: String = "Series 1") :
        this(listOf(DataSeries(label, points)))

    /** Single series from float values, with auto-generated x indices (0, 1, 2, …). */
    constructor(
        values: List<Float>,
        labels: List<String>? = null,
        seriesLabel: String = "Series 1",
    ) : this(
        listOf(
            DataSeries(
                label = seriesLabel,
                points = values.mapIndexed { i, v ->
                    DataPoint(i.toFloat(), v, labels?.getOrNull(i))
                },
            ),
        ),
    )
}
