package io.github.komposeCharts.core.data

/**
 * Top-level data container passed to all chart composables.
 *
 * @param series One or more data series. Multi-series produces grouped bars, multi-line charts, etc.
 */
data class ChartData(
    val series: List<DataSeries>,
) {
    /** Convenience constructor for single-series charts from a list of [DataPoint]. */
    constructor(
        points: List<DataPoint>,
        seriesLabel: String = "Series 1",
    ) : this(listOf(DataSeries(seriesLabel, points)))

    /** Convenience constructor for simple float value lists (auto-generates x = index). */
    constructor(
        values: List<Float>,
        seriesLabel: String = "Series 1",
        labels: List<String>? = null,
    ) : this(
        points = values.mapIndexed { i, v ->
            DataPoint(x = i.toFloat(), y = v, label = labels?.getOrNull(i))
        },
        seriesLabel = seriesLabel,
    )

    val isEmpty: Boolean get() = series.isEmpty() || series.all { it.points.isEmpty() }
}
