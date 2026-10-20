package io.github.komposeCharts.internal

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.geometry.Offset
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.renderer.SliceArc

internal class ChartState {
    var hidden by mutableStateOf(emptySet<Int>())
    var selection by mutableStateOf<Pair<Int, Int>?>(null)
    var pointer by mutableStateOf<Offset?>(null)
    var tooltip by mutableStateOf<String?>(null)
    // Draw results are read only by input handlers, so they do not invalidate composition.
    var hits: List<ChartHit> = emptyList()
    var slices: List<SliceArc> = emptyList()

    fun toggle(index: Int) {
        hidden = if (index in hidden) hidden - index else hidden + index
        selection = null
        tooltip = null
        hits = emptyList()
        slices = emptyList()
    }

    fun select(offset: Offset): ChartHit? {
        val hit = hits.firstOrNull { it.bounds.contains(offset) }
        selection = hit?.selection
        tooltip = null
        return hit
    }
}

/** Reset all transient state when the data changes, including obsolete legend indices. */
@Composable
internal fun rememberChartState(data: ChartData): ChartState = remember(data) { ChartState() }

/** Preserve slots so colors, animation, and callbacks continue to use original indices. */
internal fun visibleChartData(data: ChartData, hidden: Set<Int>): ChartData = ChartData(
    data.series.mapIndexed { index, series ->
        if (index in hidden) series.copy(points = emptyList()) else series
    },
)

internal fun formatChartValue(value: Float): String =
    if (value == value.toLong().toFloat()) value.toLong().toString() else value.toString()
