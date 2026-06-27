package io.github.komposeCharts.sample.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography

@Composable
internal fun ChartPreviewCard(
    accent: Color = AppColors.AccentDefault,
    onReplay: () -> Unit = {},
    chart: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(AppDimen.CornerLarge)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .border(AppDimen.BorderWidth, AppColors.BorderSubtle, shape)
            .background(AppColors.Surface)
            .padding(AppDimen.Spacing_16dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = AppDimen.Spacing_12dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "PREVIEW",
                color = AppColors.TextFaint,
                fontSize = AppTypography.Mono,
                fontFamily = FontFamily.Monospace,
                letterSpacing = AppTypography.MonoTracking,
            )
            Text(
                text = "\u21BB Replay",
                modifier = Modifier.clickable(onClick = onReplay),
                color = accent,
                fontSize = AppTypography.Mono,
                fontFamily = FontFamily.Monospace,
            )
        }

        chart()
    }
}
