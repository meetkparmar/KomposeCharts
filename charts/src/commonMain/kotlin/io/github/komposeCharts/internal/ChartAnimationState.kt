package io.github.komposeCharts.internal

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import io.github.komposeCharts.core.animation.ChartAnimation

/**
 * Holds the animated fraction (0 → 1) for a chart series entry animation.
 */
class ChartAnimationState internal constructor(
    private val animatable: Animatable<Float, *>,
) {
    /** Current animation progress from 0 (not started) to 1 (complete). */
    val fraction: Float get() = animatable.value
}

/**
 * Creates and remembers a [ChartAnimationState] that animates from 0 to 1.
 *
 * The animation restarts whenever [dataKey] changes (typically `data.hashCode()`).
 * Per-series stagger is applied via [seriesIndex] * [ChartAnimation.staggerMs].
 */
@Composable
fun rememberChartAnimationState(
    animation: ChartAnimation,
    dataKey: Any,
    seriesIndex: Int = 0,
): ChartAnimationState {
    val animatable = remember { Animatable(0f) }
    val state = remember { ChartAnimationState(animatable) }

    LaunchedEffect(dataKey) {
        if (animation.enabled && animation.durationMs > 0) {
            animatable.snapTo(0f)
            val delay = seriesIndex * animation.staggerMs
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = animation.durationMs,
                    delayMillis = delay,
                ),
            )
        } else {
            animatable.snapTo(1f)
        }
    }

    return state
}
