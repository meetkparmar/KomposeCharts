package io.github.komposeCharts.core.axis

/**
 * An immutable numeric range for a chart axis.
 *
 * @param min Lower bound of the range.
 * @param max Upper bound of the range (must be strictly greater than [min]).
 */
data class AxisRange(val min: Float, val max: Float) {

    init {
        require(max > min) { "max ($max) must be greater than min ($min)" }
    }

    /** The total span of the range (max - min). */
    val span: Float get() = max - min

    /** Normalizes [value] into [0, 1] within this range. Values outside the range produce values outside [0, 1]. */
    fun normalize(value: Float): Float = (value - min) / span

    /** Clamps [value] to lie within [min, max]. */
    fun clamp(value: Float): Float = value.coerceIn(min, max)
}
