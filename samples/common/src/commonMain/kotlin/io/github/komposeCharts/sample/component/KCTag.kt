package io.github.komposeCharts.sample.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun KCTag(
    label: String,
    modifier: Modifier = Modifier,
    accent: Color = AppColors.AccentDefault,
) {
    Text(
        text = label,
        modifier = modifier
            .clip(RoundedCornerShape(AppDimen.CornerSmall))
            .background(AppColors.SurfaceChip)
            .padding(horizontal = 8.dp, vertical = 3.dp),
        color = accent,
        fontSize = AppTypography.Mono,
        fontFamily = FontFamily.Monospace,
        lineHeight = 15.4.sp,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Preview
@Composable
private fun KCTagPreview() {
    KCPreviewContainer {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            KCTag(label = "BarChart")
            KCTag(label = "LineChart")
            KCTag(label = "PieChart")
        }
    }
}
