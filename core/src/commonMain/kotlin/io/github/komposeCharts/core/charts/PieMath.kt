package io.github.komposeCharts.core.charts

/**
 * A computed pie/donut slice.
 *
 * @param index Original index into the input value list.
 * @param value The (sanitized, non-negative) value of this slice.
 * @param fraction This slice's share of the total (0..1).
 * @param startAngle Start angle in degrees (clockwise from 3 o'clock; -90 = top).
 * @param sweepAngle Angular extent in degrees.
 */
data class PieSlice(
    val index: Int,
    val value: Float,
    val fraction: Float,
    val startAngle: Float,
    val sweepAngle: Float,
)

/**
 * Pure-Kotlin geometry helpers for pie/donut charts (no Compose dependency).
 */
object PieMath {

    /**
     * Converts raw [values] into cumulative [PieSlice]s.
     *
     * Negative and NaN values are treated as zero. Index alignment with the input
     * is preserved, so a zero-value slice still appears (with a zero sweep) at its
     * original index. Returns an empty list when the total is not positive.
     *
     * @param startAngle Angle of the first slice's start edge, in degrees.
     */
    fun computeSlices(values: List<Float>, startAngle: Float = -90f): List<PieSlice> {
        val sanitized = values.map { if (it.isNaN() || it < 0f) 0f else it }
        val total = sanitized.sum()
        if (total <= 0f) return emptyList()

        var angle = startAngle
        return sanitized.mapIndexed { i, v ->
            val fraction = v / total
            val sweep = fraction * 360f
            val slice = PieSlice(i, v, fraction, angle, sweep)
            angle += sweep
            slice
        }
    }
}
