package io.github.komposeCharts.sample.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun KCChip(
    label: String,
    modifier: Modifier = Modifier,
    active: Boolean = true,
    accent: Color = AppColors.AccentDefault,
    onToggle: ((Boolean) -> Unit)? = null,
) {
    var isActive by remember(active) { mutableStateOf(active) }

    val shape = RoundedCornerShape(AppDimen.CornerPill)
    val bgColor = if (isActive) accent else AppColors.SurfaceChip
    val textColor = if (isActive) AppColors.White else AppColors.TextSecondary
    val borderColor = if (isActive) accent else AppColors.Border

    Text(
        text = label,
        modifier = modifier
            .clip(shape)
            .border(AppDimen.BorderWidth, borderColor, shape)
            .background(bgColor)
            .clickable {
                isActive = !isActive
                onToggle?.invoke(isActive)
            }
            .padding(horizontal = 13.dp, vertical = 7.dp),
        color = textColor,
        fontSize = AppTypography.Chip,
        fontFamily = FontFamily.Monospace,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
private fun KCChipPreview() {
    KCPreviewContainer {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KCChip(label = "Animate", active = true)
            KCChip(label = "Grid", active = false)
            KCChip(label = "Markers", active = true)
            KCChip(label = "Legend", active = false)
        }
    }
}
