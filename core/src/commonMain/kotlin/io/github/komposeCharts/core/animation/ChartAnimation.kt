package io.github.komposeCharts.core.animation

/**
 * Configuration for chart load/re-load animations.
 *
 * @param enabled Whether animation plays at all.
 * @param durationMs Total animation duration in milliseconds.
 * @param staggerMs Per-series stagger delay in milliseconds (multi-series charts).
 */
data class ChartAnimation(
    val enabled: Boolean = true,
    val durationMs: Int = 600,
    val staggerMs: Int = 80,
) {
    companion object {
        /** Default animation: 600ms, 80ms stagger between series. */
        val Default = ChartAnimation(enabled = true, durationMs = 600, staggerMs = 80)

        /** Disables all animation (instant render). */
        val None = ChartAnimation(enabled = false, durationMs = 0, staggerMs = 0)

        /** Fast animation for dashboards or frequent data updates. */
        val Fast = ChartAnimation(enabled = true, durationMs = 300, staggerMs = 40)
    }
}
