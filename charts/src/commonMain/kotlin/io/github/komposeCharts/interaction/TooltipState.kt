package io.github.komposeCharts.interaction

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import io.github.komposeCharts.core.data.DataPoint

/**
 * Holds the current tooltip display state for a chart.
 *
 * Obtain an instance via [rememberTooltipState] and pass it to the chart composable.
 */
class TooltipState {
    var isVisible: Boolean by mutableStateOf(false)
        internal set
    var tapOffset: Offset by mutableStateOf(Offset.Zero)
        internal set
    var point: DataPoint? by mutableStateOf(null)
        internal set
    var seriesLabel: String by mutableStateOf("")
        internal set

    internal fun show(offset: Offset, point: DataPoint, seriesLabel: String) {
        this.tapOffset = offset
        this.point = point
        this.seriesLabel = seriesLabel
        this.isVisible = true
    }

    internal fun dismiss() {
        isVisible = false
    }
}

/** Creates and remembers a [TooltipState] across recompositions. */
@Composable
fun rememberTooltipState(): TooltipState = remember { TooltipState() }
