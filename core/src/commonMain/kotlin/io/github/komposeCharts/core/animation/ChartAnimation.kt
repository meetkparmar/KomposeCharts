package io.github.komposeCharts.core.animation

/**
 * Configuration for chart entry animations.
 *
 * This is a pure data class with no Compose dependency, so it can live in the `:core` module.
 * The actual Compose animation is driven by [ChartAnimationState] in the `:charts` module.
 *
 * @param enabled Whether animation is active.
 * @param durationMs Total duration of the animation in milliseconds.
 * @param staggerMs Delay between successive series animations (creates a cascade effect).
 */
data class ChartAnimation(
    val enabled: Boolean = true,
    val durationMs: Int = 600,
    val staggerMs: Int = 80,
) {
    companion object {
        /** Standard animation: 600 ms duration, 80 ms stagger. */
        val Default = ChartAnimation()

        /** No animation: instant rendering. */
        val None = ChartAnimation(enabled = false, durationMs = 0, staggerMs = 0)

        /** Faster animation: 300 ms duration, 40 ms stagger. */
        val Fast = ChartAnimation(durationMs = 300, staggerMs = 40)
    }
}
