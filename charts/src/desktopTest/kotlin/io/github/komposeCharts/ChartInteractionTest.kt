package io.github.komposeCharts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.charts.*
import io.github.komposeCharts.core.animation.ChartAnimation
import io.github.komposeCharts.core.data.ChartData
import io.github.komposeCharts.internal.ChartState
import io.github.komposeCharts.internal.chartInput
import io.github.komposeCharts.internal.rememberChartAnimationState
import io.github.komposeCharts.style.*
import org.junit.Rule
import org.junit.Test
import kotlin.test.*

class ChartInteractionTest {
    @get:Rule val compose = createComposeRule()

    @Test fun activePointerHandlerUsesUpdatedCallback() {
        var version by mutableStateOf(1)
        var received = 0
        compose.setContent {
            val capturedVersion = version
            val state = remember { ChartState() }
            Box(Modifier.size(100.dp).testTag("input").chartInput(state) { received = capturedVersion })
        }
        compose.onNodeWithTag("input").performTouchInput { click() }
        assertEquals(1, received)
        compose.runOnIdle { version = 2 }
        compose.onNodeWithTag("input").performTouchInput { click() }
        assertEquals(2, received)
    }

    @Test fun barTooltipCanBeEnabledAfterFirstTap() {
        var tooltip by mutableStateOf(false)
        compose.setContent {
            BarChart(
                ChartData(values = listOf(10f), labels = listOf("A")), Modifier.size(400.dp, 300.dp).testTag("chart"),
                BarChartStyle(animation = ChartAnimation.None, tooltipStyle = if (tooltip) TooltipStyle() else null),
            )
        }
        compose.onNodeWithTag("chart").performTouchInput { click(Offset(width / 2f, height / 2f)) }
        compose.onNodeWithText("A: 10").assertDoesNotExist()
        compose.runOnIdle { tooltip = true }
        compose.onNodeWithTag("chart").performTouchInput { click(Offset(width / 2f, height / 2f)) }
        compose.onNodeWithText("A: 10").assertExists()
    }

    @Test fun pieTooltipUsesNewDataAfterInteraction() {
        var value by mutableStateOf(10f)
        compose.setContent {
            PieChart(
                ChartData(values = listOf(value, value), labels = listOf("A", "B")),
                Modifier.size(400.dp, 300.dp).testTag("chart"),
                PieChartStyle(animation = ChartAnimation.None, tooltipStyle = TooltipStyle()),
            )
        }
        fun tapRight() = compose.onNodeWithTag("chart").performTouchInput { click(Offset(width * .7f, height * .45f)) }
        tapRight()
        compose.onNodeWithText("A: 50%").assertExists()
        compose.runOnIdle { value = 100f }
        tapRight()
        compose.onNodeWithText("A: 50%").assertExists()
    }

    @Test fun disablingAnimationSnapsToCompleteWithoutChangingData() {
        var config by mutableStateOf(ChartAnimation(durationMs = 10000))
        var fraction = 0f
        compose.mainClock.autoAdvance = false
        compose.setContent { fraction = rememberChartAnimationState(config, "same data").fraction }
        compose.mainClock.advanceTimeBy(100)
        assertTrue(fraction < 1f)
        compose.runOnIdle { config = ChartAnimation.None }
        compose.mainClock.advanceTimeByFrame()
        compose.mainClock.advanceTimeByFrame()
        compose.runOnIdle { assertEquals(1f, fraction) }
    }
    @Test fun hiddenLegendSeriesKeepsOriginalCallbackIndex() {
        var received = -1
        val data = ChartData(listOf(
            io.github.komposeCharts.core.data.DataSeries("A", listOf(io.github.komposeCharts.core.data.DataPoint(0f, 60f))),
            io.github.komposeCharts.core.data.DataSeries("B", listOf(io.github.komposeCharts.core.data.DataPoint(0f, 70f))),
        ))
        compose.setContent {
            BarChart(data, Modifier.size(400.dp, 300.dp).testTag("chart"),
                BarChartStyle(animation = ChartAnimation.None, legendStyle = LegendStyle(interactive = true)),
                onBarClick = { index, _ -> received = index })
        }
        compose.onNodeWithText("A").performClick()
        compose.onNodeWithTag("chart").performTouchInput { click(Offset(width / 2f, height * .45f)) }
        assertEquals(1, received)
    }

    @Test fun lineChartInvokesReplacementCallbackAfterFirstTap() {
        var version by mutableStateOf(1)
        var received = 0
        compose.setContent {
            val captured = version
            LineChart(ChartData(values = listOf(10f, 10f, 10f)), Modifier.size(400.dp, 300.dp).testTag("chart"),
                LineChartStyle(animation = ChartAnimation.None), onPointClick = { _, _ -> received = captured })
        }
        // At density 1 the center point is on the top plot boundary (40 px + content padding + row centering).
        compose.onNodeWithTag("chart").performTouchInput { click(Offset(width / 2f, 73f)) }
        assertEquals(1, received)
        compose.runOnIdle { version = 2 }
        compose.onNodeWithTag("chart").performTouchInput { click(Offset(width / 2f, 73f)) }
        assertEquals(2, received)
    }

}
