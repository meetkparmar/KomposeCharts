package io.github.komposeCharts.core.axis

/**
 * Represents the visible range on a single axis.
 *
 * @param min Minimum value (inclusive).
 * @param max Maximum value (inclusive). Must be > min.
 */
data class AxisRange(
    val min: Float,
    val max: Float,
) {
    init {
        require(max > min) { "AxisRange.max ($max) must be > min ($min)" }
    }

    val span: Float get() = max - min

    /** Maps a data value to a [0, 1] normalized fraction within this range. */
    fun normalize(value: Float): Float = (value - min) / span

    /** Clamps a value to [min, max]. */
    fun clamp(value: Float): Float = value.coerceIn(min, max)
}
