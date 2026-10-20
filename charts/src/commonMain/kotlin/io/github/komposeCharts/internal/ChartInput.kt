package io.github.komposeCharts.internal

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import io.github.komposeCharts.style.CrosshairStyle

/** Keep the active gesture coroutine connected to the latest data, style, and callback. */
@Composable
internal fun Modifier.chartInput(
    state: ChartState,
    crosshair: CrosshairStyle? = null,
    onTap: (Offset) -> Unit,
): Modifier {
    val currentTap by rememberUpdatedState(onTap)
    return pointerInput(state) { detectTapGestures { currentTap(it) } }
        .pointerInput(state, crosshair) {
            if (crosshair != null) {
                detectDragGestures(
                    onDragStart = { state.pointer = it },
                    onDrag = { change, _ -> state.pointer = change.position; change.consume() },
                    onDragEnd = { state.pointer = null },
                    onDragCancel = { state.pointer = null },
                )
            }
        }
        .pointerInput(state, crosshair) {
            if (crosshair != null) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        when (event.type) {
                            PointerEventType.Move -> state.pointer = event.changes.firstOrNull()?.position
                            PointerEventType.Exit -> state.pointer = null
                        }
                    }
                }
            }
        }
}
