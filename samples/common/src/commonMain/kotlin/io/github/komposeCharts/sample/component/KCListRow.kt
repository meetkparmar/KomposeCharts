package io.github.komposeCharts.sample.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.komposeCharts.sample.design.AppColors
import io.github.komposeCharts.sample.design.AppDimen
import io.github.komposeCharts.sample.design.AppTypography
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun KCListRow(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    tag: String? = null,
    accent: Color = AppColors.AccentDefault,
) {
    val shape = RoundedCornerShape(AppDimen.CornerLarge)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .border(AppDimen.BorderWidth, AppColors.BorderSubtle, shape)
            .background(AppColors.Surface)
            .clickable(onClick = onClick)
            .padding(14.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = AppColors.TextPrimary,
                fontSize = AppTypography.ListTitle,
                fontWeight = FontWeight.SemiBold,
            )

            if (subtitle != null) {
                Spacer(Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    color = AppColors.TextMuted,
                    fontSize = AppTypography.Description,
                    lineHeight = (AppTypography.Description.value * 1.4f).sp,
                )
            }

            if (tag != null) {
                Spacer(Modifier.height(7.dp))
                KCTag(label = tag, accent = accent)
            }
        }

        Text(
            text = "\u203A", // ›
            fontSize = 22.sp,
            fontWeight = FontWeight.Light,
            color = AppColors.Chevron,
        )
    }
}

@Preview
@Composable
private fun KCListRowFullPreview() {
    KCPreviewContainer {
        KCListRow(
            title = "Bar Chart",
            subtitle = "Compare values across categories.",
            tag = "BarChart",
            onClick = {},
        )
    }
}

@Preview
@Composable
private fun KCListRowGroupPreview() {
    KCPreviewContainer {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            KCListRow(
                title = "Line Chart",
                subtitle = "Track trends over time.",
                tag = "LineChart",
                onClick = {},
            )
            KCListRow(
                title = "Pie Chart",
                onClick = {},
            )
        }
    }
}
