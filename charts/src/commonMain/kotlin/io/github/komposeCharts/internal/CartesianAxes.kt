package io.github.komposeCharts.internal

import io.github.komposeCharts.core.axis.AxisRange
import io.github.komposeCharts.core.axis.AxisRangeCalculator
import io.github.komposeCharts.core.charts.BarMath
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.style.BarGrouping

internal data class CartesianAxes(
    val x: AxisRange,
    val y: AxisRange,
    val xTicks: List<Float>,
    val yTicks: List<Float>,
    val labels: List<String?>,
)

internal fun barAxes(data: ChartData, grouping: BarGrouping, tickCount: Int): CartesianAxes {
    val count = data.series.maxOfOrNull { it.points.size } ?: 0
    val rawY = when (grouping) {
        BarGrouping.GROUPED -> AxisRangeCalculator.fromData(data).second
        BarGrouping.STACKED -> BarMath.stackedRange(data)
        BarGrouping.PERCENT_STACKED -> AxisRange(0f, 100f)
    }
    val (y, ticks) = AxisRangeCalculator.nice(rawY.min, rawY.max, tickCount)
    return CartesianAxes(
        AxisRange(-0.5f, maxOf(0.5f, count - 0.5f)), y,
        (0 until count).map { it.toFloat() }, ticks,
        data.series.firstOrNull { it.points.isNotEmpty() }?.points?.map { it.label } ?: emptyList(),
    )
}

internal fun lineAxes(data: ChartData, tickCount: Int): CartesianAxes {
    val (x, rawY) = AxisRangeCalculator.fromData(data)
    val (y, ticks) = AxisRangeCalculator.nice(rawY.min, rawY.max, tickCount)
    val points = data.series.firstOrNull { it.points.isNotEmpty() }?.points?.filterNot { it.isMissing } ?: emptyList()
    return CartesianAxes(x, y, points.map { it.x }, ticks, points.map { it.label })
}
