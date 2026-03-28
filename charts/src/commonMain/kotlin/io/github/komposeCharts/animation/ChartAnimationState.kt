package io.github.komposeCharts.animation

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import io.github.komposeCharts.core.animation.ChartAnimation

/**
 * Holds the animated fraction (0f → 1f) for a single chart or single series.
 */
class ChartAnimationState internal constructor(
    internal val animatable: Animatable<Float, *>,
) {
    /** Current animation progress in [0f, 1f]. Use this in your DrawScope. */
    val fraction: Float get() = animatable.value
}

/**
 * Creates and remembers a [ChartAnimationState] that plays 0→1 whenever [dataKey] changes.
 *
 * The animation does **not** restart on style or theme changes — only when the data identity
 * (as determined by [dataKey]) changes.
 *
 * @param animation Animation configuration from the chart style.
 * @param dataKey An identity key for the current data (e.g. `data.hashCode()`). When this
 *   changes the animation restarts.
 * @param seriesIndex Series index used to calculate stagger delay.
 */
@Composable
fun rememberChartAnimationState(
    animation: ChartAnimation,
    dataKey: Any,
    seriesIndex: Int = 0,
): ChartAnimationState {
    val animatable = remember { Animatable(if (animation.enabled) 0f else 1f) }
    val state = remember(animatable) { ChartAnimationState(animatable) }

    LaunchedEffect(dataKey) {
        if (!animation.enabled) {
            animatable.snapTo(1f)
            return@LaunchedEffect
        }
        animatable.snapTo(0f)
        val delayMs = (seriesIndex * animation.staggerMs).toLong()
        if (delayMs > 0) kotlinx.coroutines.delay(delayMs)
        animatable.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = animation.durationMs),
        )
    }

    return state
}
