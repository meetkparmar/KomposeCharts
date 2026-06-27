package io.github.komposeCharts.style

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Visual style for chart tooltips shown on tap/hover.
 */
data class TooltipStyle(
    val backgroundColor: Color = Color(0xE6333333),
    val textColor: Color = Color.White,
    val cornerRadius: Dp = 8.dp,
    val padding: Dp = 8.dp,
)
